package org.circle8.integration.punto_reciclaje;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(ApiTestExtension.class)
class PuntoReciclajeGetTest {

	@Test
	void testGetOkThenReturnPuntoReciclaje() {
		RestAssured.given()
			.get("/reciclador/1/punto_reciclaje/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
		;
	}

	@Test
	void testGetParamErrorThenReturnBadRequest() {
		RestAssured.given()
			.get("/reciclador/1/punto_reciclaje/e")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
		;
	}

	@Test
	void testGetIdNotExistThenReturnNotFound() {
		RestAssured.given()
			.get("/reciclador/1/punto_reciclaje/99999999")
			.then()
			.statusCode(404)
			.body("code", equalTo("NOT_FOUND"))
		;
	}

}
