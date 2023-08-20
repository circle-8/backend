package org.circle8.integration.residuo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;

@ExtendWith(ApiTestExtension.class)
class NotificacionSolicitudResiduoTest {

	@Test void testNotificacionRetiroNotFoundResiduo() {
		requests("100", "1").forEach(r -> RestAssured.given()
			.post("/residuo/100/notificacion/1")
			.then()
			.statusCode(404)
			.body("message", containsString("residuo")));
	}

	@Test void testNotificacionRetiroFulfilledResiduo() {
		requests("3", "1").forEach(r -> RestAssured.given()
			.post("/residuo/3/notificacion/1")
			.then()
			.statusCode(400)
			.body("message", stringContainsInOrder("residuo", "retirado")));
	}

	@Test void testNotificacionRetiroNotFoundPunto() {
		requests("1", "100").forEach(r -> RestAssured.given()
			.post("/residuo/1/notificacion/100")
			.then()
			.statusCode(404)
			.body("message", containsString("punto")));
	}

	@Test void testNotificacionRetiroWrongTipoResiduo() {
		requests("1", "5").forEach(r -> RestAssured.given()
			.post("/residuo/1/notificacion/5")
			.then()
			.statusCode(400)
			.body("message", stringContainsInOrder("no admite", "tipo de residuo")));
	}

	@Test void testNotificacionRetiroSameCiudadano() {
		requests("1", "1").forEach(r -> RestAssured.given()
			.post(r)
			.then()
			.statusCode(400)
			.body("message", containsString("pertenece")));
	}

	@Test void testNotificacionRetiroOK() {
		RestAssured.given()
			.post("/residuo/4/notificacion/5")
			.then()
			.statusCode(200)
			.body("id", is(notNullValue()))
			.body("solicitanteId", equalTo(2))
			.body("solicitadoId", equalTo(1))
			.body("estado", equalTo("PENDIENTE"))
			.body("cancelador", is(nullValue()))
		;
	}

	@Test void testNotificacionDepositoOK() {
		RestAssured.given()
			.post("/residuo/4/notificacion/deposito/5")
			.then()
			.statusCode(200)
			.body("id", is(notNullValue()))
			.body("solicitanteId", equalTo(1))
			.body("solicitadoId", equalTo(2))
			.body("estado", equalTo("PENDIENTE"))
			.body("cancelador", is(nullValue()))
		;
	}

	@Test void testNotificacionRetiroWrongIds() {
		requests("aoeu", "aoeu").forEach(r -> RestAssured.given()
			.post(r)
			.then()
			.statusCode(400)
			.body("message", stringContainsInOrder("ids", "numéricos")));
	}

	List<String> requests(String id, String puntoId) {
		return List.of(
			String.format("/residuo/%s/notificacion/%s", id, puntoId),
			String.format("/residuo/%s/notificacion/deposito/%s", id, puntoId)
		);
	}
}
