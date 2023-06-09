package org.circle8.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.circle8.response.ApiResponse;
import org.circle8.response.ListResponse;
import org.circle8.response.TipoUsuarioResponse;
import org.circle8.response.UserResponse;

import java.util.List;

public class UserController {
	private static final UserResponse mock = UserResponse.builder()
		.id(1)
		.username("username")
		.tipoUsuario(TipoUsuarioResponse.CIUDADANO)
		.build();

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
	 */
	public ApiResponse token(Context ctx) {
		ctx.cookie("token", "token-str");
		return new ApiResponse() {
			@Override
			public HttpStatus status() {
				return HttpStatus.ACCEPTED;
			}
		};
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
