package org.circle8.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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
		.body("data", hasSize(2))
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
			.get("/solicitudes?expand=residuo&expand=ciudadanos")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].residuo.id", not(nullValue()))
			.body("data[0].residuo.descripcion", not(nullValue()))
			.body("data[0].solicitante.id", not(nullValue()))
			.body("data[0].solicitante.nombre", not(nullValue()))
			.body("data[0].solicitado.id", not(nullValue()))
			.body("data[0].solicitado.nombre", not(nullValue()))
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
	void testListWithotExistSolicitadoIdFilter() {
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
