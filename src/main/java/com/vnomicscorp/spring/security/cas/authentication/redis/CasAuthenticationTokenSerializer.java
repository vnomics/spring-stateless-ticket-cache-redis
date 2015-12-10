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
