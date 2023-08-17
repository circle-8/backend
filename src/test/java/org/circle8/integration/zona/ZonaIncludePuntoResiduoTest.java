package org.circle8.integration.zona;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@ExtendWith(ApiTestExtension.class)
class ZonaIncludePuntoResiduoTest {

	@Test
	void testIncludeOk() {
		RestAssured.given()
			.post("/punto_residuo/2/zona/2")
			.then()
			.statusCode(200)
			.body("puntosResiduos.id", hasItem(equalTo(2)))
		;
	}

	@Test
	void testPuntoNotIncludeInZona() {
		RestAssured.given()
			.post("/punto_residuo/3/zona/2")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testAlreadyExist() {
		RestAssured.given()
			.post("/punto_residuo/1/zona/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testNotFoundZona() {
		RestAssured.given()
			.post("/punto_residuo/2/zona/0")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testNotFoundPunto() {
		RestAssured.given()
			.post("/punto_residuo/0/zona/2")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testWithOutPunto() {
		RestAssured.given()
			.post("/punto_residuo//zona/2")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testInvalidPuntoId() {
		RestAssured.given()
			.post("/punto_residuo/a/zona/2")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutZona() {
		RestAssured.given()
			.post("/punto_residuo/2/zona/")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testInvalidZonaId() {
		RestAssured.given()
			.post("/punto_residuo/2/zona/a")
			.then()
			.statusCode(400)
		;
	}
}
