package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.notNullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransporteInicioTest {

	@Test
	void testInicioOk() {
		RestAssured.given()
			.post("/transporte/1/inicio")
			.then()
			.statusCode(200)
			.body("fechaFin", notNullValue())	
		;
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
			.post("/transporte/0/inicio")
			.then()
			.statusCode(404)		
		;
	}
	
	@Test
	void testInvalidId() {
		RestAssured.given()
			.post("/transporte/a/inicio")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithOutId() {
		RestAssured.given()
			.post("/transporte/inicio")
			.then()
			.statusCode(404)		
		;
	}
}
