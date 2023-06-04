package org.circle8.controller;

import com.google.inject.Inject;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;
import org.circle8.request.user.TokenRequest;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.TipoUsuarioResponse;
import org.circle8.response.TokenResponse;
import org.circle8.response.UserResponse;
import org.circle8.security.JwtService;

import java.util.List;

public class UserController {
	private static final UserResponse mock = UserResponse.builder()
		.id(1)
		.username("username")
		.tipoUsuario(TipoUsuarioResponse.CIUDADANO)
		.build();

	private final JwtService jwtService;

	@Inject
	public UserController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * GET /users
	 */
	public ApiResponse list(Context ctx) {
		final var l = List.of(
			mock,
			mock.toBuilder().id(2).build()
		);

		return new ListResponse<>(0, 1, 2, null, null, l);
	}

	/**
	 * GET /user/{id}
	 */
	public ApiResponse get(Context ctx) {
		return mock.toBuilder().id(Integer.parseInt(ctx.pathParam("id"))).build();
	}

	/**
	 * POST /token
	 * El token es devuelto luego de hacer el login in.
	 * Con este token el usuario puede acceder la resto de la API.
	 * Se devuelve dentro de una Cookie como en el body de la response
	 */
	public ApiResponse token(Context ctx) {
		final TokenRequest req = ctx.bodyAsClass(TokenRequest.class);
		var jwt = jwtService.token(String.valueOf(mock.id), req.username);

		var cookie = new Cookie("access_token", jwt);
		cookie.setHttpOnly(true);
		cookie.setSameSite(SameSite.STRICT);
		cookie.setSecure(true);
		ctx.cookie(cookie);

		return new TokenResponse(jwt, mock);
	}

	/**
	 * POST /user
	 */
	public ApiResponse post(Context ctx) {
		return mock;
	}

	/**
	 * PUT /user/password
	 */
	public ApiResponse restorePassword(Context ctx) {
		return mock;
	}
}
