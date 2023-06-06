package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;
import org.circle8.controller.request.user.TokenRequest;
import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.TipoUsuarioResponse;
import org.circle8.controller.response.TokenResponse;
import org.circle8.controller.response.UserResponse;
import org.circle8.dto.UserDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.security.JwtService;
import org.circle8.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final UserResponse mock = UserResponse.builder()
		.id(1)
		.username("username")
		.tipoUsuario(TipoUsuarioResponse.CIUDADANO)
		.build();

	private final JwtService jwtService;
	private final UserService service;

	@Inject
	public UserController(JwtService jwtService, UserService userService) {
		this.jwtService = jwtService;
		this.service = userService;
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
		var req = ctx.bodyAsClass(UserRequest.class);

		var valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		var dto = UserDto.from(req);
		try {
			dto = service.save(dto, req.password);
		} catch ( ServiceError e ) {
			logger.error("[Request:{}] error saving new user", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), e.getDevMessage());
		}

		return dto.toResponse();
	}

	/**
	 * PUT /user/password
	 */
	public ApiResponse restorePassword(Context ctx) {
		return mock;
	}
}
