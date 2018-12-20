# Spring Stateless Ticket Cache Redis

This project implements `org.springframework.security.cas.authentication.StatelessTicketCache` for storing stateless CAS tickets in a Redis database. This supports stateless authentication scheme in a highly available environment. This project depends on the [Jedis](https://github.com/xetorthio/jedis) Redis library.

## Maven Coordinates

```xml
<dependency>
    <groupId>com.vnomicscorp</groupId>
    <artifactId>spring-stateless-ticket-cache-redis</artifactId>
    <version>1.0.3</version>
</dependency>
```

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


## Building

	mvn install

### Key setup
You must have gpg key to do a full build.  Create one on linux with:

	gpg --gen-key
	
If you need to supply a passphrase to the build then use 

	mvn install -D"gpg.passphrase=thephrase"
	
See maven-gpg-plugin documentation for further details. 