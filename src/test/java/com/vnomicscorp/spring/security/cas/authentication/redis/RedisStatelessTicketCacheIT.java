/**
 * Copyright 2015 Vnomics Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vnomicscorp.spring.security.cas.authentication.redis;

import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Integration test that verifies we can connect to a redis cluster correctly and our
 * {@link RedisStatelessTicketCache} works properly. This test requires docker to be installed.
 *
 */
public class RedisStatelessTicketCacheIT {

	private static final int REDIS_DEFAULT_PORT = 6379;

	@Rule
	public GenericContainer redis = new GenericContainer("redis:5")
			.withExposedPorts(REDIS_DEFAULT_PORT);

	private static final String USERNAME = "dave";
	private static final String CREDENTIALS = "ST-whatever";
	private static final String KEY = "key";
	private static final String ROLE = "role";

	private RedisStatelessTicketCache cache;

	@Before
	public void setup() {
		JedisPool jedisPool = new JedisPool(redis.getContainerIpAddress(), redis.getMappedPort(REDIS_DEFAULT_PORT));
		cache = new RedisStatelessTicketCache(jedisPool);
		cache.setExpirationSeconds(60);
	}

	@Test
	public void doCrud() {
		CasAuthenticationToken token = makeToken();
		// Cleanup delete
		cache.removeTicketFromCache(CREDENTIALS);
		// Create
		cache.putTicketInCache(token);
		// Read
		assertTokenEquals(token, cache.getByTicketId(CREDENTIALS));
		// Update
		token.setAuthenticated(false);
		cache.putTicketInCache(token);
		assertTokenEquals(token, cache.getByTicketId(CREDENTIALS));
		// Delete
		cache.removeTicketFromCache(token);
		assertNull(cache.getByTicketId(CREDENTIALS));
	}

	@Test
	public void expiration() throws InterruptedException {
		cache.setExpirationSeconds(2);
		CasAuthenticationToken token = makeToken();
		// Cleanup delete
		cache.removeTicketFromCache(CREDENTIALS);
		// Create
		cache.putTicketInCache(token);
		// Read
		assertTokenEquals(token, cache.getByTicketId(CREDENTIALS));
		// Wait for ticket to expire
		Thread.sleep(6000l);
		// Read again
		assertEquals(null, cache.getByTicketId(CREDENTIALS));
	}

	private CasAuthenticationToken makeToken() {
		return new CasAuthenticationToken(KEY, USERNAME, CREDENTIALS, Arrays.asList(new SimpleGrantedAuthority(ROLE)),
			new User(USERNAME, CREDENTIALS, Arrays.asList(new SimpleGrantedAuthority(ROLE))), new AssertionImpl(
				USERNAME));
	}

	private void assertTokenEquals(CasAuthenticationToken expected, CasAuthenticationToken got) {
		assertEquals(expected.getName(), got.getName());
		assertEquals(expected.isAuthenticated(), got.isAuthenticated());
		assertEquals(expected.getAuthorities(), got.getAuthorities());
		assertEquals(expected.getCredentials(), got.getCredentials());
		assertEquals(expected.getDetails(), got.getDetails());
		assertEquals(expected.getKeyHash(), got.getKeyHash());
		assertEquals(expected.getPrincipal(), got.getPrincipal());
		assertEquals(expected.getUserDetails(), got.getUserDetails());
	}

	@Bean
	public PropertySourcesPlaceholderConfigurer propertyConfig() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
