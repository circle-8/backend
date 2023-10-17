package org.circle8.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import org.circle8.controller.request.plan.PostPlanRequest;
import org.circle8.controller.request.plan.PutPlanRequest;
import org.circle8.controller.request.recorrido.PostRecorridoRequest;
import org.circle8.controller.request.transporte.TransportePutRequest;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.controller.response.ListResponse;
import org.circle8.controller.response.PlanResponse;
import org.circle8.controller.response.SuccessResponse;
import org.circle8.dto.PlanDto;
import org.circle8.dto.RecorridoDto;
import org.circle8.dto.TransporteDto;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.service.PlanService;
import org.circle8.service.RecorridoService;

import java.math.BigDecimal;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Singleton
@Slf4j
public class PlanController {

	private final PlanService service;

	@Inject
	public PlanController(PlanService service) {
		this.service = service;
	}

	private final PlanResponse mock = PlanResponse.builder()
		.id(1)
		.nombre("plan prueba")
		.precio(BigDecimal.TEN)
		.mesesRenovacion(3)
		.cantUsuarios(1)
		.build();

	/**
	 * GET /plan/{id}
	 */
	public ApiResponse get(Context ctx) {
		final long id;

		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del plan debe ser numérico", "");
		}
		try {
			return this.service.get(id).toResponse();
		} catch ( ServiceError e ) {
			log.error("[Request: id={}] error approve solicitud", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * PUT /plan/{id}
	 */
	public ApiResponse put(Context ctx) {
		final long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id del plan debe ser numérico", "");
		}

		val req = ctx.bodyAsClass(PutPlanRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		try {
			val tr = PlanDto.from(req);
			tr.id = id;
			return this.service.update(tr).toResponse();
		} catch ( ServiceError e ) {
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * POST /plan
	 */
	public ApiResponse post(Context ctx) {
		val req = ctx.bodyAsClass(PostPlanRequest.class);
		val valid = req.valid();
		if ( !valid.valid() )
			return new ErrorResponse(ErrorCode.BAD_REQUEST, valid.message(), "");

		var dto = PlanDto.from(req);
		try {
			return service.save(dto).toResponse();
		} catch (ServiceError e) {
			log.error("[Request:{}] error saving new recorrido", req, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	public ApiResponse delete(Context ctx) {
		long id;
		try {
			id = Long.parseLong(ctx.pathParam("id"));
		} catch ( NumberFormatException e) {
			return new ErrorResponse(ErrorCode.BAD_REQUEST, "El id de plan debe ser numérico", "");
		}

		try{
			this.service.delete(id);
			return new SuccessResponse();
		} catch ( ServiceError e ) {
			log.error("[id:{} error deleting plan", id, e);
			return new ErrorResponse(ErrorCode.INTERNAL_ERROR, e.getMessage(), e.getDevMessage());
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}

	/**
	 * GET /planes
	 */
	public ApiResponse list(Context ctx) {
		try {
			val planes = service.list();
			return new ListResponse<>(planes.stream().map(PlanDto::toResponse).toList());
		} catch (ServiceError e) {
			log.error("[Request: filter={}] error listing planes", e);
			return new ErrorResponse(e);
		} catch ( ServiceException e ) {
			return new ErrorResponse(e);
		}
	}
}
