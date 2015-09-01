package com.vnomicscorp.spring.security.providers.cas.redis;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

public class RedisStatelessTicketCacheTest extends EasyMockSupport {
	private RedisStatelessTicketCache cache;
	private JedisPool jedisPool;
	private Jedis jedis;
	private CasAuthenticationTokenSerializer serializer;
	private CasAuthenticationToken token;

	@Before
	public void setup() {
		jedisPool = createStrictMock(JedisPool.class);
		jedis = createStrictMock(Jedis.class);
		serializer = createStrictMock(CasAuthenticationTokenSerializer.class);
		token = createStrictMock(CasAuthenticationToken.class);
		cache = new RedisStatelessTicketCache(jedisPool);
		cache.setCasAuthenticationTokenSerializer(serializer);
		resetAll();
	}

	@Test
	public void getByTicketId() throws CasAuthenticationTokenSerializerException {
		String st = "ST-dddddd";
		String val = "someval";
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(jedis.get(st)).andReturn(val);
		jedis.close();
		expectLastCall();
		expect(serializer.deserialize(val)).andReturn(token);
		replayAll();
		assertEquals(token, cache.getByTicketId(st));
		verifyAll();
	}

	@Test(expected = NullPointerException.class)
	public void getByTicketIdNull() throws CasAuthenticationTokenSerializerException {
		cache.getByTicketId(null);
	}

	@Test(expected = RuntimeException.class)
	public void getByTicketSerializationException() throws CasAuthenticationTokenSerializerException {
		String st = "ST-dddddd";
		String val = "someval";
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(jedis.get(st)).andReturn(val);
		jedis.close();
		expectLastCall();
		expect(serializer.deserialize(val)).andThrow(new CasAuthenticationTokenSerializerException("blah"));
		replayAll();
		cache.getByTicketId(st);
	}

	@Test
	public void putTicketInCacheNoExpiration() throws CasAuthenticationTokenSerializerException {
		cache.setExpirationSeconds(-1);
		String st = "ST-dddddd";
		String val = "someval";
		expect(token.getCredentials()).andReturn(st);
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(serializer.serialize(token)).andReturn(val);
		Transaction transaction = createStrictMock(Transaction.class);
		expect(jedis.multi()).andReturn(transaction);
		expect(transaction.set(st, val)).andReturn(null);
		expect(transaction.exec()).andReturn(new ArrayList<Object>());
		jedis.close();
		expectLastCall();
		replayAll();
		cache.putTicketInCache(token);
		verifyAll();
	}

	@Test
	public void putTicketInCacheWithExpiration() throws CasAuthenticationTokenSerializerException {
		Integer expirationSecs = 5;
		cache.setExpirationSeconds(5);
		String st = "ST-dddddd";
		String val = "someval";
		expect(token.getCredentials()).andReturn(st);
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(serializer.serialize(token)).andReturn(val);
		Transaction transaction = createStrictMock(Transaction.class);
		expect(jedis.multi()).andReturn(transaction);
		expect(transaction.set(st, val)).andReturn(null);
		expect(transaction.expire(st, expirationSecs)).andReturn(null);
		expect(transaction.exec()).andReturn(new ArrayList<Object>());
		jedis.close();
		expectLastCall();
		replayAll();
		cache.putTicketInCache(token);
		verifyAll();
	}

	@Test(expected = NullPointerException.class)
	public void putTicketInCacheNull() {
		cache.putTicketInCache(null);
	}

	@Test(expected = RuntimeException.class)
	public void putTicketInCacheSerializationException() throws CasAuthenticationTokenSerializerException {
		String st = "ST-dddddd";
		expect(token.getCredentials()).andReturn(st);
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(serializer.serialize(token)).andThrow(new CasAuthenticationTokenSerializerException("blah"));
		jedis.close();
		expectLastCall();
		replayAll();
		cache.putTicketInCache(token);
	}

	@Test
	public void removeTicketFromCache() {
		String st = "ST-dddddd";
		expect(token.getCredentials()).andReturn(st);
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(jedis.del(st)).andReturn(0l);
		jedis.close();
		expectLastCall();
		replayAll();
		cache.removeTicketFromCache(token);
		verifyAll();
	}

	@Test
	public void removeTicketFromCache2() {
		String st = "ST-dddddd";
		expect(jedisPool.getResource()).andReturn(jedis);
		expect(jedis.del(st)).andReturn(0l);
		jedis.close();
		expectLastCall();
		replayAll();
		cache.removeTicketFromCache(st);
		verifyAll();
	}

	@Test(expected = NullPointerException.class)
	public void removeTicketFromCacheNull() {
		cache.removeTicketFromCache((CasAuthenticationToken) null);
	}

	@Test(expected = NullPointerException.class)
	public void removeTicketFromCacheNull2() {
		cache.removeTicketFromCache((String) null);
	}
}
