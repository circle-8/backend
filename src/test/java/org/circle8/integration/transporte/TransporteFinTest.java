package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransporteFinTest {

	@Test
	void testPagoOk() {
		RestAssured.given()
			.post("/transporte/5/fin")
			.then()
			.statusCode(200)
			.body("fechaFin", notNullValue())		
		;
	}
	
	@Test
	void testPagoWithResiduosPendientes() {
		RestAssured.given()
			.post("/transporte/1/fin")
			.then()
			.statusCode(400)	
			.body("message", stringContainsInOrder("finalizar el transporte", "retirado todos los residuos"))
		;
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
			.post("/transporte/0/fin")
			.then()
			.statusCode(404)		
		;
	}
	
	@Test
	void testInvalidId() {
		RestAssured.given()
			.post("/transporte/a/fin")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithOutId() {
		RestAssured.given()
			.post("/transporte/fin")
			.then()
			.statusCode(404)		
		;
	}
}
