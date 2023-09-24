package org.circle8.route;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.circle8.controller.ConsejoController;
import org.circle8.controller.OrganizacionController;
import org.circle8.controller.PlanController;
import org.circle8.controller.PuntoReciclajeController;
import org.circle8.controller.PuntoResiduoController;
import org.circle8.controller.PuntoVerdeController;
import org.circle8.controller.RecorridoController;
import org.circle8.controller.ResiduoController;
import org.circle8.controller.SolicitudController;
import org.circle8.controller.SuscripcionController;
import org.circle8.controller.TipoResiduoController;
import org.circle8.controller.TransaccionController;
import org.circle8.controller.TransporteController;
import org.circle8.controller.UserController;
import org.circle8.controller.ZonaController;
import org.circle8.controller.response.ApiResponse;
import org.circle8.controller.response.ErrorCode;
import org.circle8.controller.response.ErrorResponse;
import org.circle8.security.JwtService;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.function.Function;

@Slf4j
public class Routes {
	public static final String ERROR_INESPERADO = "Ha ocurrido un error inesperado";

	private final ResiduoController residuoController;
	private final PuntoReciclajeController puntoReciclajeController;
	private final TransaccionController transaccionController;
	private final ZonaController zonaController;
	private final RecorridoController recorridoController;
	private final TransporteController transporteController;
	private final UserController userController;
	private final PuntoResiduoController puntoResiduoController;
	private final PlanController planController;
	private final SuscripcionController suscripcionController;
	private final TipoResiduoController tipoResiduoController;
	private final PuntoVerdeController puntoVerdeController;
	private final SolicitudController solicitudController;
	private final OrganizacionController organizacionController;
	private final ConsejoController consejoController;

	private final JwtService jwtService;

	private final Gson gson;
	private final Configuration cfg;

	@Inject
	public Routes(
		Gson gson,
		Configuration cfg,
		JwtService jwtService,
		ResiduoController residuoController,
		PuntoReciclajeController puntoReciclajeController,
		TransaccionController transaccionController,
		ZonaController zonaController,
		RecorridoController recorridoController,
		TransporteController transporteController,
		UserController userController,
		PuntoResiduoController puntoResiduoController,
		PlanController planController,
		SuscripcionController suscripcionController,
		TipoResiduoController tipoResiduoController,
		PuntoVerdeController puntoVerdeController,
		SolicitudController solicitudController,
		OrganizacionController organizacionController,
		ConsejoController consejoController
	) {
		this.gson = gson;
		this.cfg = cfg;
		this.jwtService = jwtService;
		this.residuoController = residuoController;
		this.puntoReciclajeController = puntoReciclajeController;
		this.transaccionController = transaccionController;
		this.zonaController = zonaController;
		this.recorridoController = recorridoController;
		this.transporteController = transporteController;
		this.userController = userController;
		this.puntoResiduoController = puntoResiduoController;
		this.planController = planController;
		this.suscripcionController = suscripcionController;
		this.tipoResiduoController = tipoResiduoController;
		this.puntoVerdeController = puntoVerdeController;
		this.solicitudController = solicitudController;
		this.organizacionController = organizacionController;
		this.consejoController = consejoController;
	}

	public Javalin initRoutes() {
		return Javalin.create(c -> c.jsonMapper(getJsonMapper()))
			// RESIDUOS
			.get("/residuos", result(residuoController::list))
			.post("/residuo", result(residuoController::post))
			.get("/residuo/{id}", result(residuoController::get))
			.put("/residuo/{id}", result(residuoController::put))
			.delete("/residuo/{id}", result(residuoController::delete))
			.post("/residuo/{id}/reciclaje", result(residuoController::reciclaje))
			.delete("/residuo/{id}/reciclaje", result(residuoController::deleteReciclaje))
			.post("/residuo/{id}/notificacion/{punto_reciclaje_id}", result(residuoController::notificacion))
			.post("/residuo/{id}/notificacion/deposito/{punto_reciclaje_id}", result(residuoController::notificacionDeposito))
			.post("/residuo/{id}/fulfill", result(residuoController::fulfill))
			.post("/residuo/{id}/unfulfilled", result(residuoController::unfulfilled))
			// PUNTO RECICLAJE
			.get("/puntos_reciclaje", result(puntoReciclajeController::list))
			.get("/reciclador/{reciclador_id}/punto_reciclaje/{id}", result(puntoReciclajeController::get))
			.put("/reciclador/{reciclador_id}/punto_reciclaje/{id}", result(puntoReciclajeController::put))
			.delete("/reciclador/{reciclador_id}/punto_reciclaje/{id}", result(puntoReciclajeController::delete))
			.post("/reciclador/{reciclador_id}/punto_reciclaje", result(puntoReciclajeController::post))
			.post("/reciclador/{reciclador_id}/punto_reciclaje/{id}", result(puntoReciclajeController::post)) // TODO: remove
			.post("/reciclador/{reciclador_id}/punto_reciclaje/{id}/notificacion/{id_residuo}", result(puntoReciclajeController::notificacion))
			// TRANSACCION
			.get("/transacciones", result(transaccionController::list))
			.get("/transaccion/{id}", result(transaccionController::get))
			.put("/transaccion/{id}", result(transaccionController::put))
			.delete("/transaccion/{id}", result(transaccionController::delete))
			.post("/transaccion", result(transaccionController::post))
			.put("/transaccion/{id}/residuo/{id_residuo}", result(transaccionController::addResiduo))
			.delete("/transaccion/{id}/residuo/{id_residuo}", result(transaccionController::removeResiduo))
			.post("/transaccion/{id}/transporte/{id_transporte}", result(transaccionController::setTransporte))
			.delete("/transaccion/{id}/transporte/{id_transporte}", result(transaccionController::unsetTransporte))
			.post("/transaccion/{id}/transporte", result(transaccionController::solicitudTransporte))
			.delete("/transaccion/{id}/transporte", result(transaccionController::deleteTransporte))
			// ZONA
			.get("/zonas", result(zonaController::list))
			.get("/organizacion/{organizacion_id}/zona/{id}", result(zonaController::get))
			.put("/organizacion/{organizacion_id}/zona/{id}", result(zonaController::put))
			.delete("/organizacion/{organizacion_id}/zona/{id}", result(zonaController::delete))
			.post("/organizacion/{organizacion_id}/zona", result(zonaController::post))
			.post("/punto_residuo/{punto_residuo_id}/zona/{id}", result(zonaController::includePuntoResiduo))
			.delete("/punto_residuo/{punto_residuo_id}/zona/{id}", result(zonaController::excludePuntoResiduo))
			// RECORRIDO
			.get("/recorridos", result(recorridoController::list))
			.get("/recorrido/{id}", result(recorridoController::get))
			.post("/organizacion/{organizacion_id}/zona/{zona_id}/recorrido", result(recorridoController::post))
			.put("/organizacion/{organizacion_id}/zona/{zona_id}/recorrido/{id}", result(recorridoController::put))
			.delete("/organizacion/{organizacion_id}/zona/{zona_id}/recorrido/{id}", result(recorridoController::delete))
			.post("/recorrido/{id}/inicio", result(recorridoController::inicio))
			.post("/recorrido/{id}/fin", result(recorridoController::fin))
			// Transporte
			.get("/transportes", result(transporteController::list))
			.get("/transporte/{id}", result(transporteController::get))
			.post("/transporte/{id}/precio", result(transporteController::setPrecio))
			.post("/transporte/{id}/inicio", result(transporteController::inicio))
			.post("/transporte/{id}/fin", result(transporteController::fin))
			.post("/transporte/{id}/pago", result(transporteController::confirmarPago))
			.post("/transporte/{id}/confirmacion_entrega", result(transporteController::confirmarEntrega))
			.put("/transporte/{id}", result(transporteController::put))
			// USER
			.get("/users", result(userController::list))
			.get("/user/{id}", result(userController::get))
			.post("/token", noAuthRequired(userController::token))
			.post("/refresh_token", noAuthRequired(userController::refreshToken))
			.post("/user", noAuthRequired(userController::post))
			.put("/user/password", noAuthRequired(userController::restorePassword))
			.put("/user/{id}", noAuthRequired(userController::put))
			// PUNTOS RESIDUO
			.get("/ciudadano/{ciudadano_id}/punto_residuo/{id}", result(puntoResiduoController::get))
			.get("/puntos_residuo", result(puntoResiduoController::list))
			.post("/ciudadano/{ciudadano_id}/punto_residuo/", result(puntoResiduoController::post))
			.put("/ciudadano/{ciudadano_id}/punto_residuo/{id}", result(puntoResiduoController::put))
			// PLAN
			.get("/planes", result(planController::list))
			.get("/plan/{id}", result(planController::get))
			.put("/plan/{id}", result(planController::put))
			.delete("/plan/{id}", result(planController::delete))
			.post("/plan", result(planController::post))
			// SUSCRIPCION
			.get("/suscripciones", result(suscripcionController::list))
			.get("/user/{id_user}/suscripcion", result(suscripcionController::get))
			.post("/user/{id_user}/suscripcion", result(suscripcionController::post))
			.delete("/user/{id_user}/suscripcion", result(suscripcionController::delete))
			// TIPOS RESIDUO
			.get("/tipos_residuo", result(tipoResiduoController::list))
			.get("/tipo_residuo/{id}", result(tipoResiduoController::get))
			.put("/tipo_residuo/{id}", result(tipoResiduoController::put))
			.delete("/tipo_residuo/{id}", result(tipoResiduoController::delete))
			.post("/tipo_residuo", result(tipoResiduoController::post))
			// PUNTO VERDE
			.get("/puntos_verdes", result(puntoVerdeController::list))
			.get("/punto_verde/{id}", result(puntoVerdeController::get))
			.put("/punto_verde/{id}", result(puntoVerdeController::put))
			.delete("/punto_verde/{id}", result(puntoVerdeController::delete))
			.post("/punto_verde", result(puntoVerdeController::post))
			// SOLICITUD
			.get("/solicitudes", result(solicitudController::list))
			.get("/solicitud/{id}", result(solicitudController::get))
			.put("/solicitud/{id}/aprobar", result(solicitudController::approve))
			.put("/solicitud/{id}/cancelar", result(solicitudController::cancel))
			// ORGANIZACION
			.get("/organizacion/{id}", result(organizacionController::get))
			.delete("/organizacion/{organizacion_id}/reciclador/{reciclador_id}", result(organizacionController::removeReciclador))
			// CONSEJOS
			.get("/consejos", result(consejoController::list))
			.post("/consejo", result(consejoController::post))
			.put("/consejo/{id}", result(consejoController::put))
			.delete("/consejo/{id}", result(consejoController::delete))
			// Exceptions
			.error(HttpStatus.NOT_FOUND, ctx -> {
				if ( Strings.isNullOrEmpty(ctx.result()) || "Not Found".equalsIgnoreCase(ctx.result()) ) {
					var err = new ErrorResponse(ErrorCode.NOT_FOUND, "Recurso no existente", "");
					setContext(ctx, err);
				}
			})
			.exception(Exception.class, (e, ctx) -> {
				log.error("[path:{}] Unexpected exception. Exception handler", ctx.path(), e);
				var err = new ErrorResponse(ErrorCode.INTERNAL_ERROR, ERROR_INESPERADO, e.getMessage());
				setContext(ctx, err);
			})
			;
	}

	@NotNull
	private JsonMapper getJsonMapper() {
		return new JsonMapper() {
			@NotNull
			@Override
			public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
				return gson.fromJson(json, targetType);
			}

			@NotNull
			@Override
			public String toJsonString(@NotNull Object obj, @NotNull Type type) {
				return gson.toJson(obj, type);
			}
		};
	}

	private Handler noAuthRequired(Function<Context, ApiResponse> h) {
		return ctx -> {
			try {
				ctx.contentType(ContentType.APPLICATION_JSON);

				final ApiResponse r = h.apply(ctx);

				setContext(ctx, r);
			} catch (JsonSyntaxException e) {
				var err = new ErrorResponse(ErrorCode.BAD_REQUEST, "Se debe enviar un JSON valido", e.getMessage());
				setContext(ctx, err);
			} catch (Exception e) {
				log.error("[path:{}] Unexpected exception. No Auth exception handler", ctx.path(), e);
				var err = new ErrorResponse(ErrorCode.INTERNAL_ERROR, ERROR_INESPERADO, e.getMessage());
				setContext(ctx, err);
			}
		};
	}

	private Handler result(Function<Context, ApiResponse> h) {
		return ctx -> {
			try {
				if ( this.cfg.getBoolean("jwt.require.token") ) {
					var token = ctx.cookie("access_token");
					if (Strings.isNullOrEmpty(token)) {
						var err = new ErrorResponse(ErrorCode.TOKEN_NOT_FOUND, "Debe enviar un token", "");
						setContext(ctx, err);
						return;
					}
					if (!jwtService.isValid(token)) {
						var err = new ErrorResponse(ErrorCode.TOKEN_EXPIRED, "Token expirado", "");
						setContext(ctx, err);
						return;
					}
				}

				ctx.contentType(ContentType.APPLICATION_JSON);

				final ApiResponse r = h.apply(ctx);

				setContext(ctx, r);
			} catch (SecurityException e) {
				var err = new ErrorResponse(ErrorCode.TOKEN_ERROR, e.getMessage(), e.getMessage());
				setContext(ctx, err);
			} catch (JsonSyntaxException e) {
				var err = new ErrorResponse(ErrorCode.BAD_REQUEST, "Se debe enviar un JSON valido", e.getMessage());
				setContext(ctx, err);
			} catch (Exception e) {
				log.error("[path:{}] Unexpected exception. Result exception handler", ctx.path(), e);
				var err = new ErrorResponse(ErrorCode.INTERNAL_ERROR, ERROR_INESPERADO, e.getMessage());
				setContext(ctx, err);
			}
		};
	}

	private void setContext(Context ctx, ApiResponse res) {
		ctx.result(gson.toJson(res));
		ctx.status(res.status());
	}

}
