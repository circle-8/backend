package org.circle8.controller;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import io.javalin.http.SameSite;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.configuration2.Configuration;
import org.circle8.controller.request.user.RefreshTokenRequest;
import org.circle8.controller.request.user.TokenRequest;
import org.circle8.controller.request.user.UserPutRequest;
import org.circle8.controller.request.user.UserRequest;
import org.circle8.controller.request.user.UsersRequest;
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
import org.circle8.filter.UserFilter;
import org.circle8.security.JwtService;
import org.circle8.service.UserService;

@Singleton
@Slf4j
public class UserController {
	private static final UserResponse mock = UserResponse.builder()
		.id(1)
		.username("username")
		.tipoUsuario(TipoUsuarioResponse.CIUDADANO)
		.build();

	private final JwtService jwtService;
	private final UserService service;

	private final boolean secureTokenCookie;

	@Inject
	public UserController(JwtService jwtService, UserService userService, Configuration cfg) {
		this.jwtService = jwtService;
		this.service = userService;
		this.secureTokenCookie = cfg.getBoolean("jwt.secure.cookie");
	}

	/**
	 * GET /users
	 */
	public ApiResponse list(Context ctx) {
		val req = new UsersRequest(ctx.queryParamMap());
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		val filter = UserFilter.builder()
			.organizacionId(req.organizacionId)
			.tipoUsuario(req.tipoUsuario != null ? req.tipoUsuario.to() : null)
			.build();

		try {
			val users = service.list(filter);
			return new ListResponse<>(users.stream().map(UserDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error list users", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * GET /user/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del usuario debe ser numérico", "");
		}

		try {
			return service.get(id).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /token
	 * El token es devuelto luego de hacer el login in.
	 * Con este token el usuario puede acceder la resto de la API.
	 * Se devuelve dentro de una Cookie como en el body de la response
	 */
	public ApiResponse token(Context ctx) {
		val req = ctx.bodyAsClass(TokenRequest.class);

		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		try {
			val u = service.login(req.username, req.password);

			val jwt = jwtService.token(u);
			val refreshJwt = jwtService.refreshToken(jwt);
			setTokenCookies(ctx, jwt.token(), refreshJwt.token());

			return new TokenResponse(jwt.token(), refreshJwt.token(), u.toResponse());
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error login in", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	private void setTokenCookies(Context ctx, String jwt, String refresh) {
		val cookie = new Cookie("access_token", jwt);
		cookie.setHttpOnly(true);
		cookie.setSameSite(SameSite.STRICT);
		cookie.setSecure(secureTokenCookie);
		ctx.cookie(cookie);

		val refreshCookie = new Cookie("refresh_token", refresh);
		cookie.setHttpOnly(true);
		cookie.setSameSite(SameSite.STRICT);
		cookie.setSecure(secureTokenCookie);
		ctx.cookie(refreshCookie);
	}

	/**
	 * POST /refresh_token
	 * Utilizando el token viejo y el token de refresh, obtiene un nuevo token
	 */
	public ApiResponse refreshToken(Context ctx) {
		val req = ctx.bodyAsClass(RefreshTokenRequest.class);

		if ( Strings.isNullOrEmpty(req.refreshToken) )
			req.refreshToken = ctx.cookie("refresh_token");
		if ( Strings.isNullOrEmpty(req.accessToken) )
			req.accessToken = ctx.cookie("access_token");

		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		try {
			val jwt = jwtService.token(req.refreshToken, req.accessToken);
			val refresh = jwtService.refreshToken(jwt);
			val u = service.get(Long.parseLong(jwt.userId()));

			setTokenCookies(ctx, jwt.token(), refresh.token());

			return new TokenResponse(jwt.token(), refresh.token(), u.toResponse());
		} catch ( SecurityException e ) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage(), "");
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error login in", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}

	}

	/**
	 * POST /user
	 */
	public ApiResponse post(Context ctx) {
		val req = ctx.bodyAsClass(UserRequest.class);

		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		var dto = UserDto.from(req);
		try {
			dto = service.save(dto, req.password);
		} catch ( ServiceError e ) {
			log.error("[Request:{}] error saving new user", req, e);
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

	/**
	 * PUT /user/id
	 */
	public ApiResponse put(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del usuario debe ser numérico", "");
		}

		val req = ctx.bodyAsClass(UserPutRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(valid);

		var dto = UserDto.from(req);

		try {
			dto = service.put(id, dto);
		} catch ( ServiceError e ) {
			log.error("[Request:{}, id={}] error updating user", req, id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}

		return dto.toResponse();
	}
}
