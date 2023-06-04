package org.circle8.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Map;

public class JwtService {
	private static class JWT {
		String id;
		String username;
	}

	public String token(String id, String username) {
		/* TODO: cambiar id y username por UserDto */

		Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		return Jwts.builder()
			.setSubject(id)
			.setClaims(Map.of("username", username))
			.signWith(key)
			.compact();
	}

	// public String token(String refreshToken) {

	// }

	// public String refreshToken() {

	// }

	// public boolean validToken(String token) {

	// }
}
