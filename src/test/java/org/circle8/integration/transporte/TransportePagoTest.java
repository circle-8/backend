package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransportePagoTest {

	@Test
	void testPagoOk() {
		RestAssured.given()
			.post("/transporte/1/pago")
			.then()
			.statusCode(200)
			.body("pagoConfirmado", equalTo(true))		
		;
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
			.post("/transporte/0/pago")
			.then()
			.statusCode(404)		
		;
	}
	
	@Test
	void testInvalidId() {
		RestAssured.given()
			.post("/transporte/a/pago")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithOutId() {
		RestAssured.given()
			.post("/transporte//pago")
			.then()
			.statusCode(404)		
		;
	}
}
