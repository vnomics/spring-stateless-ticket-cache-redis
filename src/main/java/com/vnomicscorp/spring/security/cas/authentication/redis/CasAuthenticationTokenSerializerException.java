package com.vnomicscorp.spring.security.cas.authentication.redis;

public class CasAuthenticationTokenSerializerException extends Exception {

	private static final long serialVersionUID = 1L;

	public CasAuthenticationTokenSerializerException(String message) {
		super(message);
	}

	public CasAuthenticationTokenSerializerException(Throwable cause) {
		super(cause);
	}

	public CasAuthenticationTokenSerializerException(String message, Throwable cause) {
		super(message, cause);
	}

}
