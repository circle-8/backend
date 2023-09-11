package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransporteConfirmarEntregaTest {

	@Test
	void testConfirmarEntregaOk() {
		RestAssured.given()
			.post("/transporte/1/confirmacion_entrega")
			.then()
			.statusCode(200)
			.body("entregaConfirmada", equalTo(true))		
		;
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
			.post("/transporte/0/confirmacion_entrega")
			.then()
			.statusCode(404)		
		;
	}
	
	@Test
	void testInvalidId() {
		RestAssured.given()
			.post("/transporte/a/confirmacion_entrega")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithOutId() {
		RestAssured.given()
			.post("/transporte//confirmacion_entrega")
			.then()
			.statusCode(404)		
		;
	}
}
