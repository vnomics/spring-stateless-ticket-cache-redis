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

/**
 * Checked exception thrown by CasAuthenticationTokenSerializer
 * 
 * @author Sam Nelson
 *
 */
public class CasAuthenticationTokenSerializerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 *            The message to include on the exception
	 */
	public CasAuthenticationTokenSerializerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 *            The exception that caused this exception to be thrown
	 */
	public CasAuthenticationTokenSerializerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 *            The message to include on the exception
	 * @param cause
	 *            The exception that caused this exception to be thrown
	 */
	public CasAuthenticationTokenSerializerException(String message, Throwable cause) {
		super(message, cause);
	}

}
