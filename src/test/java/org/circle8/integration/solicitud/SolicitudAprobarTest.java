package org.circle8.integration.solicitud;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class SolicitudAprobarTest {

	@Test
	void testAProbarOk() {		
		RestAssured.given()
		.put("/solicitud/1/aprobar")
		.then()
		.statusCode(200)
		.body("estado", equalTo("APROBADA"))
		;
	}
	
	@Test
	void testNotFoundSolicitudID() {		
		RestAssured.given()
		.put("/solicitud/0/aprobar")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutSolicitudID() {		
		RestAssured.given()
		.put("/solicitud//aprobar")
		.then()
		.statusCode(404)
		;
	}
}
