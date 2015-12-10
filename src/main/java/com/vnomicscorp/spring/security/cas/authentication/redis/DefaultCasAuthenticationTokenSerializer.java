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
 * Default implementation of {@link CasAuthenticationTokenSerializer} that uses Java serialization
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
}
