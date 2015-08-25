package com.vnomicscorp.spring.security.providers.cas.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.StatelessTicketCache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * An implementation of {@link org.springframework.security.cas.authentication.StatelessTicketCache} that uses Redis for
 * a cache. Uses the Jedis Java Redis client.
 * 
 * @author Samuel Nelson
 *
 */
public class RedisStatelessTicketCache implements StatelessTicketCache {

	private Logger logger = LoggerFactory.getLogger(RedisStatelessTicketCache.class);

	private final JedisPool jedisPool;
	private CasAuthenticationTokenSerializer casAuthenticationTokenSerializer = new DefaultCasAuthenticationTokenSerializer();

	/**
	 * Creates a new instance
	 * 
	 * @param jedisPool
	 *            The pool to get instances of {@link redis.clients.jedis.Jedis} from
	 */
	public RedisStatelessTicketCache(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public CasAuthenticationToken getByTicketId(String serviceTicket) {
		if (serviceTicket == null) {
			throw new NullPointerException("Expected given serviceTicket to be not null");
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String serialized = jedis.get(serviceTicket);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cache hit: %b; service ticket: %s", serialized != null, serviceTicket));
			}
			return serialized == null ? null : casAuthenticationTokenSerializer.deserialize(serialized);
		} catch (CasAuthenticationTokenSerializerException e) {
			throw new RuntimeException("Exception encountered while serializing CasAuthenticationToken", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void putTicketInCache(CasAuthenticationToken token) {
		if (token == null) {
			throw new NullPointerException("Expected given token to be not null");
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			String serialized = casAuthenticationTokenSerializer.serialize(token);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cache put: %s", token.getCredentials().toString()));
			}
			jedis.set(token.getCredentials().toString(), serialized);
		} catch (CasAuthenticationTokenSerializerException e) {
			throw new RuntimeException("Exception encountered while deserializing CasAuthenticationToken", e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void removeTicketFromCache(CasAuthenticationToken token) {
		removeTicketFromCache(token.getCredentials().toString());
	}

	@Override
	public void removeTicketFromCache(String serviceTicket) {
		if (serviceTicket == null) {
			throw new NullPointerException("Expected given serviceTicket to be not null");
		}
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Cache remove: %s", serviceTicket));
			}
			jedis.del(serviceTicket);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Gets the serializer that will be used to serialize and deserialize
	 * {@link org.springframework.security.cas.authentication.CasAuthenticationToken} objects
	 * 
	 * @return The serializer that will be used to serialize and deserialize
	 *         {@link org.springframework.security.cas.authentication.CasAuthenticationToken} objects
	 */
	public CasAuthenticationTokenSerializer getCasAuthenticationTokenSerializer() {
		return casAuthenticationTokenSerializer;
	}

	/**
	 * Sets the serializer that will be used to serialize and deserialize
	 * {@link org.springframework.security.cas.authentication.CasAuthenticationToken} objects
	 * 
	 * @param casAuthenticationTokenSerializer
	 *            The serializer that will be used to serialize and deserialize
	 *            {@link org.springframework.security.cas.authentication.CasAuthenticationToken} objects
	 */
	public void setCasAuthenticationTokenSerializer(CasAuthenticationTokenSerializer casAuthenticationTokenSerializer) {
		this.casAuthenticationTokenSerializer = casAuthenticationTokenSerializer;
	}
}
