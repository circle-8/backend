package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PuntoReciclajeDeleteTest {

	@Test
	void testDeleteOk() {
		RestAssured.given()
			.delete("/reciclador/1/punto_reciclaje/2")
			.then()
			.statusCode(200)
		;
	}

	@Test
	void testInvalidRecicladorId() {
		RestAssured.given()
			.delete("/reciclador/asd/punto_reciclaje/47")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testInvalidPuntoReciclaje() {
		RestAssured.given()
			.delete("/reciclador/1/punto_reciclaje/asd")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWhitoutReciclajeId() {
		RestAssured.given()
			.delete("/reciclador//punto_reciclaje/1")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testWhitoutPuntoReciclaje() {
		RestAssured.given()
			.delete("/reciclador/1/punto_reciclaje/")
			.then()
			.statusCode(404)
		;
	}
}
