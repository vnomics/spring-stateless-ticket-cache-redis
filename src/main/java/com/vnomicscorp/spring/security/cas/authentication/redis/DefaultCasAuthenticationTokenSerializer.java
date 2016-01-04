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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.crypto.codec.Base64;

/**
 * Default implementation of {@link CasAuthenticationTokenSerializer} that uses
 * Java serialization
 * 
 * @author Samuel Nelson
 *
 */
public class DefaultCasAuthenticationTokenSerializer implements CasAuthenticationTokenSerializer {

	private Charset charset = Charset.forName("UTF-8");

	@Override
	public String serialize(CasAuthenticationToken token) throws CasAuthenticationTokenSerializerException {
		if (token == null) {
			throw new NullPointerException("Expected given token to be non-null");
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(token);
			oos.flush();
			return new String(Base64.encode(baos.toByteArray()), charset);
		} catch (IOException e) {
			throw new CasAuthenticationTokenSerializerException(e);
		}
	}

	@Override
	public CasAuthenticationToken deserialize(String serialized) throws CasAuthenticationTokenSerializerException {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(serialized.getBytes(charset)));
			ObjectInputStream oos = new ObjectInputStream(bais);
			return (CasAuthenticationToken) oos.readObject();
		} catch (IOException e) {
			throw new CasAuthenticationTokenSerializerException(e);
		} catch (ClassNotFoundException e) {
			throw new CasAuthenticationTokenSerializerException(e);
		}
	}

	/**
	 * Gets the charset that will be used to encode and decode strings to bytes
	 * and vice versa
	 * 
	 * @return The charset that will be used to encode and decode strings to
	 *         bytes and vice versa
	 */
	public Charset getCharset() {
		return charset;
	}

	/**
	 * Sets the charset that will be used to encode and decode strings to bytes
	 * and vice versa
	 * 
	 * @param charset
	 *            The charset that will be used to encode and decode strings to
	 *            bytes and vice versa
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
}
