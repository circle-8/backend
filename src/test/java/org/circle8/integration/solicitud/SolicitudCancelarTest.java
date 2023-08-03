package org.circle8.integration.solicitud;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class SolicitudCancelarTest {

	@Test
	void testCancelarOk() {		
		RestAssured.given()
		.put("/solicitud/1/cancelar?ciudadanoCancelaId=1")
		.then()
		.statusCode(200)
		.body("estado", equalTo("CANCELADA"))
		.body("canceladorId", equalTo(1))
		;
	}
	
	@Test
	void testNotFoundSolicitudID() {		
		RestAssured.given()
		.put("/solicitud/0/cancelar?ciudadanoCancelaId=1")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutSolicitudID() {		
		RestAssured.given()
		.put("/solicitud//cancelar?ciudadanoCancelaId=1")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutciudadanoCancelaID() {		
		RestAssured.given()
		.put("/solicitud/1/cancelar")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithInvalidciudadanoCancelaID() {		
		RestAssured.given()
		.put("/solicitud/1/cancelar?ciudadanoCancelaId=aa")
		.then()
		.statusCode(400)
		;
	}
}
