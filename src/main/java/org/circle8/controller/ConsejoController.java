package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.circle8.controller.request.consejo.PostConsejoRequest;
import org.circle8.controller.request.consejo.PutConsejoRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.dto.ConsejoDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.service.ConsejoService;
import org.circle8.update.UpdateConsejo;

@Singleton
@Slf4j
public class ConsejoController {
	private final ConsejoService service;

	@Inject
	public ConsejoController(ConsejoService service) { this.service = service; }

	public ApiResponse list(Context ctx) {
		try {
			val consejos = this.service.list();
			return new ListResponse<>(consejos.stream().map(ConsejoDto::toResponse).toList());
		} catch ( ServiceError e ) {
			log.error("error list consejos", e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	public ApiResponse put(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del consejo debe ser numérico", "");
		}

		val req = ctx.bodyAsClass(PutConsejoRequest.class);
		req.id = id;
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		try {
			service.update(UpdateConsejo.from(req));
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("error put consejos", e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	public ApiResponse post(Context ctx) {
		val req = ctx.bodyAsClass(PostConsejoRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		try {
			return service.save(ConsejoDto.from(req)).toResponse();
		} catch ( ServiceError e ) {
			log.error("error post consejos", e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	public ApiResponse delete(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del consejo debe ser numérico", "");
		}

		try {
			service.delete(id);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("error delete consejos", e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
}
