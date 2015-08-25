package com.vnomicscorp.spring.security.providers.cas.redis;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.jasig.cas.client.validation.AssertionImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class DefaultCasAuthenticationTokenSerializerTest {
	private static final String USERNAME = "dave";
	private static final String CREDENTIALS = "ST-whatever";
	private static final String KEY = "key";
	private static final String ROLE = "role";

	private DefaultCasAuthenticationTokenSerializer serializer;

	@Before
	public void setup() {
		serializer = new DefaultCasAuthenticationTokenSerializer();
	}

	/**
	 * Tests that when we deserialize a serialized object you get an object equal to the object that was serialized
	 * 
	 * @throws CasAuthenticationTokenSerializerException
	 */
	@Test
	public void identity() throws CasAuthenticationTokenSerializerException {
		CasAuthenticationToken expected = makeToken();
		CasAuthenticationToken got = serializer.deserialize(serializer.serialize(expected));
		assertTokenEquals(expected, got);
	}

	@Test(expected = NullPointerException.class)
	public void serializeNull() throws CasAuthenticationTokenSerializerException {
		serializer.serialize(null);
	}

	@Test(expected = NullPointerException.class)
	public void deserializeNull() throws CasAuthenticationTokenSerializerException {
		serializer.deserialize(null);
	}

	private CasAuthenticationToken makeToken() {
		return new CasAuthenticationToken(KEY, USERNAME, CREDENTIALS, Arrays.asList(new SimpleGrantedAuthority(ROLE)),
			new User(USERNAME, CREDENTIALS, Arrays.asList(new SimpleGrantedAuthority(ROLE))), new AssertionImpl(
				USERNAME));
	}

	private void assertTokenEquals(CasAuthenticationToken expected, CasAuthenticationToken got) {
		assertEquals(expected.getName(), got.getName());
		assertEquals(expected.isAuthenticated(), got.isAuthenticated());
		assertEquals(expected.getAuthorities(), got.getAuthorities());
		assertEquals(expected.getCredentials(), got.getCredentials());
		assertEquals(expected.getDetails(), got.getDetails());
		assertEquals(expected.getKeyHash(), got.getKeyHash());
		assertEquals(expected.getPrincipal(), got.getPrincipal());
		assertEquals(expected.getUserDetails(), got.getUserDetails());
	}
}
