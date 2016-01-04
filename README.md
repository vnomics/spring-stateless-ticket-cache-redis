# Spring Stateless Ticket Cache Redis

This project implements `org.springframework.security.cas.authentication.StatelessTicketCache` for storing stateless CAS tickets in a Redis database. This supports stateless authentication scheme in a highly available environment. This project depends on the [Jedis](https://github.com/xetorthio/jedis) Redis library.

## Usage

Usage is simple. Construct a `com.vnomicscorp.spring.security.cas.authentication.redis.RedisStatelessTicketCache` with a Redis connection, optionally set expiration time, and use the instance when configuring your CAS security provider.

```java
// Configure Redis connection
JedisPool pool = new JedisPool(new JedisPoolConfig(), hostname, port, timeout, null, database);
// Create the cache
RedisStatelessTicketCache cache = new RedisStatelessTicketCache(pool);
cache.setExpirationSeconds(expirationSeconds);
// Configure the CAS provider
casAuthenticationProvider.setStatelessTicketCache(cache);
```