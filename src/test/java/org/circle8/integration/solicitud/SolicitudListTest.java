package org.circle8.integration.solicitud;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
class SolicitudListTest {

	@Test
	void testListWithoutFilter() {
		RestAssured.given()
		.get("/solicitudes")
		.then()
		.statusCode(200)
		.body("data", hasSize(greaterThan(1)))
		.body("data[0].id", equalTo(1))
		.body("data[0].solicitanteId", equalTo(2))
		.body("data[0].solicitanteUri", equalTo("/user/2"))
		.body("data[0].solicitante", not(nullValue()))
		.body("data[0].solicitadoId", not(nullValue()))
		.body("data[0].solicitadoUri", equalTo("/user/1"))
		.body("data[0].solicitado", not(nullValue()))
		.body("data[0].estado", equalTo("PENDIENTE"))
		.body("data[1].id", equalTo(2))
		.body("data[1].estado", equalTo("EXPIRADA"))
		;
	}

	@Test
	void testListWithExpands() {
		RestAssured.given()
			.get("/solicitudes?expand=residuo&expand=ciudadanos&expand=punto_reciclaje")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.residuo.id", everyItem(notNullValue()))
			.body("data.solicitante.nombre", everyItem(notNullValue()))
			.body("data.solicitado.nombre", everyItem(notNullValue()))
			.body("data.puntoReciclaje.titulo", everyItem(notNullValue()))
		;
	}

	@Test
	void testListWithSolicitanteIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitante_id=2")
		.then()
		.statusCode(200)
		.body("data", hasSize(1))
		.body("data[0].id", equalTo(1))
		.body("data[0].solicitanteId", equalTo(2))
		.body("data[0].solicitanteUri", equalTo("/user/2"))
		.body("data[0].solicitante", not(nullValue()))
		.body("data[0].solicitadoId", not(nullValue()))
		.body("data[0].solicitadoUri", equalTo("/user/1"))
		.body("data[0].solicitado", not(nullValue()))
		.body("data[0].estado", equalTo("PENDIENTE"))
		;
	}

	@Test
	void testListWithInvalidSolicitanteIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitante_id=aaa")
		.then()
		.statusCode(400)
		;
	}

	@Test
	void testListWithNotExistSolicitanteIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitante_id=0")
		.then()
		.statusCode(200)
		.body("data", hasSize(0))
		;
	}

	@Test
	void testListWithSolicitadoIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitado_id=1")
		.then()
		.statusCode(200)
		.body("data", hasSize(1))
		.body("data[0].id", equalTo(1))
		.body("data[0].solicitanteId", equalTo(2))
		.body("data[0].solicitanteUri", equalTo("/user/2"))
		.body("data[0].solicitante", not(nullValue()))
		.body("data[0].solicitadoId", not(nullValue()))
		.body("data[0].solicitadoUri", equalTo("/user/1"))
		.body("data[0].solicitado", not(nullValue()))
		.body("data[0].estado", equalTo("PENDIENTE"))
		;
	}

	@Test
	void testListWithInvalidSolicitadoIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitado_id=aaa")
		.then()
		.statusCode(400)
		;
	}

	@Test
	void testListWithoutExistSolicitadoIdFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitado_id=0")
		.then()
		.statusCode(200)
		.body("data", hasSize(0))
		;
	}

	@Test
	void testListWithBothFilter() {
		RestAssured.given()
		.get("/solicitudes?solicitante_id=2&solicitado_id=1")
		.then()
		.statusCode(200)
		.body("data", hasSize(1))
		.body("data[0].id", equalTo(1))
		.body("data[0].solicitanteId", equalTo(2))
		.body("data[0].solicitanteUri", equalTo("/user/2"))
		.body("data[0].solicitante", not(nullValue()))
		.body("data[0].solicitadoId", not(nullValue()))
		.body("data[0].solicitadoUri", equalTo("/user/1"))
		.body("data[0].solicitado", not(nullValue()))
		.body("data[0].estado", equalTo("PENDIENTE"))
		;
	}
}
