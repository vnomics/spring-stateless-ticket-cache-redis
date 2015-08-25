package com.vnomicscorp.spring.security.providers.cas.redis;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.JedisPool;

/**
 * Integration test that verifies we can connect to a redis cluster correctly and our {@link RedisStatelessTicketCache}
 * works properly. This test is configured with the following Spring properties
 * 
 * com.vnomicscorp.spring.security.providers.cas.redis.RedisStatelessTicketCacheIT.host - The host of the Redis server
 * com.vnomicscorp.spring.security.providers.cas.redis.RedisStatelessTicketCacheIT.port - The port of the Redis server
 * 
 * @author Samuel Nelson
 *
 */
@Category(IntegrationTest.class)
@ContextConfiguration(classes = { RedisStatelessTicketCacheIT.class })
@RunWith(SpringJUnit4ClassRunner.class)
@PropertySource("classpath:application.properties")
public class RedisStatelessTicketCacheIT {

	private static final String USERNAME = "dave";
	private static final String CREDENTIALS = "ST-whatever";
	private static final String KEY = "key";
	private static final String ROLE = "role";

	@Value("${com.vnomicscorp.spring.security.providers.cas.redis.RedisStatelessTicketCacheIT.host}")
	private String host;

	@Value("${com.vnomicscorp.spring.security.providers.cas.redis.RedisStatelessTicketCacheIT.port}")
	private Integer port;

	private RedisStatelessTicketCache cache;

	@Before
	public void setup() {
		JedisPool jedisPool = new JedisPool(host, port);
		cache = new RedisStatelessTicketCache(jedisPool);
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
