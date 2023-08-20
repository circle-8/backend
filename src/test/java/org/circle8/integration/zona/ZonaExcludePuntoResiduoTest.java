package org.circle8.integration.zona;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

@ExtendWith(ApiTestExtension.class)
public class ZonaExcludePuntoResiduoTest {

	@Test
	void testExcludeOk() {
		RestAssured.given()
			.delete("/punto_residuo/1/zona/1")
			.then()
			.statusCode(200)
			.body("puntosResiduos", hasSize(1))
		;

		RestAssured.given()
		.delete("/punto_residuo/1/zona/2")
		.then()
		.statusCode(200)
		.body("puntosResiduos.id", not(hasItem(equalTo(1))))
	;
	}

	@Test
	void testNotFoundZona() {
		RestAssured.given()
			.delete("/punto_residuo/2/zona/0")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testNotFoundPunto() {
		RestAssured.given()
			.delete("/punto_residuo/0/zona/2")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testWithOutPunto() {
		RestAssured.given()
			.delete("/punto_residuo//zona/2")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testInvalidPuntoId() {
		RestAssured.given()
			.delete("/punto_residuo/a/zona/2")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutZona() {
		RestAssured.given()
			.delete("/punto_residuo/2/zona/")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testInvalidZonaId() {
		RestAssured.given()
			.delete("/punto_residuo/2/zona/a")
			.then()
			.statusCode(400)
		;
	}
}
