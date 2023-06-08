package org.circle8.security;

import com.google.inject.Singleton;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.circle8.dto.UserDto;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Singleton
public class JwtService {
	private static final long EXPIRATION_MINUTES = 1L;
	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	/**
	 * Obtiene un nuevo JWT a partir de un usuario.
	 */
	public String token(UserDto user) {
		final var expiration = Date.from(LocalDateTime.now()
			.plusMinutes(EXPIRATION_MINUTES)
			.atZone(ZoneId.systemDefault())
			.toInstant()
		);

		return Jwts.builder()
			.setId(String.valueOf(user.id))
			.setSubject(user.username)
			.setExpiration(expiration)
			.signWith(KEY)
			.compact();
	}

	// public String token(String refreshToken) {

	// }

	// public String refreshToken() {

	// }

	/**
	 * @param token el token a validar
	 * @return indica si el token es válido o no. Para saber si es válido, revisa su
	 *         fecha de expiracion
	 * @throws SecurityException en caso de que el token no sea válido por mala firma
	 */
	public boolean isValid(String token) throws SecurityException {
		try {
			Jwts.parserBuilder()
				.setSigningKey(KEY)
				.build().parseClaimsJws(token);

			return true;
		} catch ( ExpiredJwtException e ) {
			return false;
		} catch ( JwtException e ) {
			throw new SecurityException("El JWT es invalido", e);
		}
	}
}
