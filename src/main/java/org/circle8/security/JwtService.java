package org.circle8.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.apache.commons.configuration2.Configuration;
import org.circle8.dto.UserDto;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.val;

@Singleton
public class JwtService {
	private final long expirationMinutes;
	private final long refreshExpirationMinutes;
	private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	/**
	 * El mapa mantiene los tokens de refresh.
	 * Su key es el token de refresh, y su value es el JWT utilizado al momento de crear el refresh
	 * token. Para que un refresh token sea válido, su JWT asociado también lo debe ser.
	 */
	private final Map<String, String> refreshes = new ConcurrentHashMap<>();

	public record Jwt(String token, String userId, String username){}

	@Inject
	public JwtService(Configuration c) {
		expirationMinutes = c.getLong("jwt.expiration.minutes");
		refreshExpirationMinutes = c.getLong("jwt.refresh.expiration.minutes");
	}

	/**
	 * Obtiene un nuevo JWT a partir de un usuario.
	 */
	public Jwt token(UserDto user) {
		return createJwt(String.valueOf(user.id), user.username, expirationMinutes);
	}

	/**
	 * @param refreshToken el token de refresh
	 * @param jwt el token ya expirado
	 * @return un nuevo token
	 * @throws SecurityException en caso de que haya algun error de firma o expiracion de token
	 *                           de refresh
	 */
	public Jwt token(String refreshToken, String jwt) throws SecurityException {
		try {
			val claims = Jwts.parserBuilder()
				.setSigningKey(KEY)
				.build().parseClaimsJws(refreshToken).getBody();

			val expiredToken = this.refreshes.remove(refreshToken);
			if ( expiredToken == null )
				throw new SecurityException("Refresh Token invalido");

			if ( !jwt.equals(expiredToken) )
				throw new SecurityException("Token invalido");

			val userId = claims.getId();
			val username = claims.getSubject();
			return createJwt(userId, username, expirationMinutes);
		} catch ( ExpiredJwtException e ) {
			throw new SecurityException("Refresh Token expirado");
		} catch ( JwtException e ) {
			throw new SecurityException("El Refresh Token es invalido", e);
		}
	}

	/**
	 * Crea un token de refresh a partir de un token JWT
	 */
	public Jwt refreshToken(Jwt jwt) {
		val refresh = createJwt(jwt.userId, jwt.username, refreshExpirationMinutes);
		this.refreshes.put(refresh.token, jwt.token);

		return refresh;
	}

	private Jwt createJwt(String userId, String username, long expirationMinutes) {
		val expiration = Date.from(LocalDateTime.now()
			.plusMinutes(expirationMinutes)
			.atZone(ZoneId.systemDefault())
			.toInstant()
		);

		val jwt = Jwts.builder()
			.setId(userId)
			.setSubject(username)
			.setExpiration(expiration)
			.signWith(KEY)
			.compact();

		return new Jwt(jwt, userId, username);
	}

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
