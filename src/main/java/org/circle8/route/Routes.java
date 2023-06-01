package org.circle8.route;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.circle8.controller.PlanController;
import org.circle8.controller.PuntoReciclajeController;
import org.circle8.controller.PuntoResiduoController;
import org.circle8.controller.PuntoVerdeController;
import org.circle8.controller.RecorridoController;
import org.circle8.controller.ResiduoController;
import org.circle8.controller.SuscripcionController;
import org.circle8.controller.TipoResiduoController;
import org.circle8.controller.TransaccionController;
import org.circle8.controller.TransporteController;
import org.circle8.controller.UserController;
import org.circle8.controller.ZonaController;
import org.circle8.response.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class Routes {
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

	private static final Gson gson = new GsonBuilder() // TODO: esto se puede llevar a DependencyInjection
		.registerTypeAdapter(
			LocalDateTime.class,
			(JsonSerializer<LocalDateTime>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
		)
		.registerTypeAdapter(
			LocalDate.class,
			(JsonSerializer<LocalDate>) (o, type, jsonSerializationContext) -> new JsonPrimitive(o.toString())
		)
		.create();

	@Inject
	public Routes(
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
		PuntoVerdeController puntoVerdeController
	) {
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
	}

	public Javalin initRoutes() {
		return Javalin.create()
			// RESIDUOS
			.get("/residuos", result(residuoController::list))
			.post("/residuo", result(residuoController::post))
			.get("/residuo/{id}", result(residuoController::get))
			.put("/residuo/{id}", result(residuoController::put))
			.delete("/residuo/{id}", result(residuoController::delete))
			.post("/residuo/{id}/reciclaje", result(residuoController::reciclaje))
			.delete("/residuo/{id}/reciclaje", result(residuoController::deleteReciclaje))
			.post("/residuo/{id}/notificacion/{id_punto_reciclaje}", result(residuoController::notificacion))
			.post("/residuo/{id}/fulfill", result(residuoController::fulfill))
			.post("/residuo/{id}/unfulfilled", result(residuoController::unfulfilled))
			// PUNTO RECICLAJE
			.get("/puntos_reciclaje", result(puntoReciclajeController::list))
			.get("/reciclador/{id_reciclador}/punto_reciclaje/{id}", result(puntoReciclajeController::get))
			.put("/reciclador/{id_reciclador}/punto_reciclaje/{id}", result(puntoReciclajeController::put))
			.delete("/reciclador/{id_reciclador}/punto_reciclaje/{id}", result(puntoReciclajeController::delete))
			.post("/reciclador/{id_reciclador}/punto_reciclaje", result(puntoReciclajeController::post))
			.post("/reciclador/{id_reciclador}/punto_reciclaje/{id}/notificacion/{id_residuo}", result(puntoReciclajeController::notificacion))
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
			.post("/transaccion/{id}/solicitud_transporte", result(transaccionController::solicitudTransporte))
			// ZONA
			.get("/zonas", result(zonaController::list))
			.get("/organizacion/{id_organizacion}/zona/{id}", result(zonaController::get))
			.put("/organizacion/{id_organizacion}/zona/{id}", result(zonaController::put))
			.delete("/organizacion/{id_organizacion}/zona/{id}", result(zonaController::delete))
			.post("/organizacion/{id_organizacion}/zona", result(zonaController::post))
			.post("/punto_residuo/{id_punto_residuo}/zona/{id}", result(zonaController::includePuntoResiduo))
			.delete("/punto_residuo/{id_punto_residuo}/zona/{id}", result(zonaController::excludePuntoResiduo))
			// RECORRIDO
			.get("/recorridos", result(recorridoController::list))
			.get("/recorrido/{id}", result(recorridoController::get))
			.post("/organizacion/{id_organizacion}/zona/{id_zona}/recorrido", result(recorridoController::post))
			.put("/organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}", result(recorridoController::put))
			.delete("/organizacion/{id_organizacion}/zona/{id_zona}/recorrido/{id}", result(recorridoController::delete))
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
			// USER
			.get("/users", result(userController::list))
			.get("/user/{id}", result(userController::get))
			.post("/token", result(userController::token))
			.post("/user", result(userController::post))
			.put("/user/password", result(userController::restorePassword))
			// PUNTOS RESIDUO
			.get("/puntos_residuo", result(puntoResiduoController::list))
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
			;
	}

	private Handler result(Function<Context, ApiResponse> h) {
		return ctx -> {
			final ApiResponse r = h.apply(ctx);
			ctx.contentType(ContentType.APPLICATION_JSON);
			ctx.result(gson.toJson(r));
			ctx.status(r.status());
		};
	}

}
