package org.circle8.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class SolicitudGetTest {

	@Test
	void testGetOK() {
		RestAssured.given()
		.get("/solicitud/1")
		.then()
		.statusCode(200)
		.body("id", equalTo(1))
		.body("solicitanteId", equalTo(2))
		.body("solicitanteUri", equalTo("/user/2"))
		.body("solicitante", not(nullValue()))
		.body("solicitadoId", not(nullValue()))
		.body("solicitadoUri", equalTo("/user/1"))
		.body("solicitado", not(nullValue()))
		.body("estado", equalTo("PENDIENTE"))
		;
	}
	
	@Test
	void testGetOKEstadoExpirado() {
		RestAssured.given()
		.get("/solicitud/2")
		.then()
		.statusCode(200)
		.body("estado", equalTo("EXPIRADA"))
		;
	}	
	
	@Test
	void testNotFound() {
		RestAssured.given()
		.get("/solicitud/0")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutSolicitudId() {
		RestAssured.given()
		.get("/solicitud/")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testInvalidSolicitudId() {
		RestAssured.given()
		.get("/solicitud/das")
		.then()
		.statusCode(400)
		;
	}
}
