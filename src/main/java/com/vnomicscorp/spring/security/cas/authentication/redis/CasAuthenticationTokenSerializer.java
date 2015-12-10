package com.vnomicscorp.spring.security.cas.authentication.redis;

import org.springframework.security.cas.authentication.CasAuthenticationToken;

/**
 * An interface which defines implementations that serialize and deserialize
 * {@link org.springframework.security.cas.authentication.CasAuthenticationToken} to and from {@link java.lang.String}
 * 
 * @author Samuel Nelson
 *
 */
public interface CasAuthenticationTokenSerializer {
	/**
	 * Serializes the given authentication token as a {@java.util.String}
	 * 
	 * @param token
	 *            The token to serialize
	 * @return The serialized token
	 * @throws CasAuthenticationTokenSerializerException
	 */
	String serialize(CasAuthenticationToken token) throws CasAuthenticationTokenSerializerException;

	/**
	 * Deserializes the given string into an instance of
	 * {@link org.springframework.security.cas.authentication.CasAuthenticationToken}
	 * 
	 * @param serialized
	 *            The result of serializing a
	 *            {@link org.springframework.security.cas.authentication.CasAuthenticationToken}
	 * @return The deserialized token
	 * @throws CasAuthenticationTokenSerializerException
	 */
	CasAuthenticationToken deserialize(String serialized) throws CasAuthenticationTokenSerializerException;
}
