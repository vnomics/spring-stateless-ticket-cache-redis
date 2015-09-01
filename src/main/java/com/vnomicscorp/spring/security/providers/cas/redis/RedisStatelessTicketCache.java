package com.vnomicscorp.spring.security.providers.cas.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.StatelessTicketCache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

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
	private Integer expirationSeconds = -1;

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
			logger.debug("Cache hit: {}; service ticket: {}", serialized != null, serviceTicket);
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
			String key = token.getCredentials().toString();
			logger.debug("Cache put: {}", key);
			Transaction transaction = jedis.multi();
			transaction.set(key, serialized);
			if (expirationSeconds != -1) {
				transaction.expire(key, expirationSeconds);
			}
			transaction.exec();
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
			logger.debug("Cache remove: {}", serviceTicket);
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

	/**
	 * Gets the number of seconds that tickets are cached for before being expired
	 * 
	 * @return The number of seconds that tickets are cached for before being expired
	 */
	public Integer getExpirationSeconds() {
		return expirationSeconds;
	}

	/**
	 * Sets the number of seconds that tickets are cached for before being expired
	 * 
	 * @param expirationSeconds
	 *            The number of seconds that tickets are cached for before being expired
	 */
	public void setExpirationSeconds(Integer expirationSeconds) {
		this.expirationSeconds = expirationSeconds;
	}
}
