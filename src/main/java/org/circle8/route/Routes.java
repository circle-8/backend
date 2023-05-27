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
import org.circle8.controller.PuntoReciclajeController;
import org.circle8.controller.ResiduoController;
import org.circle8.response.ApiResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

public class Routes {
	private final ResiduoController residuoController;
	private final PuntoReciclajeController puntoReciclajeController;

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
		PuntoReciclajeController puntoReciclajeController
	) {
		this.residuoController = residuoController;
		this.puntoReciclajeController = puntoReciclajeController;
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
