package org.circle8.integration.suscripcion;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class SuscripcionGetTest {

	@Test
	void testGetOk() {
		RestAssured.given()
			.get("/user/1/suscripcion")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("ultimaRenovacion", equalTo("2023-09-21"))
			.body("proximaRenovacion", equalTo("2023-09-21"))
			.body("plan.id", equalTo(1))
			.body("plan.nombre", equalTo("Free"))
			.body("plan.precio", equalTo(0))
			.body("plan.mesesRenovacion", equalTo(12))
			.body("plan.cantidadUsuarios", equalTo(3))
		;
	}

	@Test
	void testNotFoundSuscripcion() {
		RestAssured.given()
		.get("/user/2/suscripcion")
		.then()
		.statusCode(404)
		;
	}
	

	@Test
	void testNotFoundUser() {
		RestAssured.given()
		.get("/user/0/suscripcion")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutUserId() {
		RestAssured.given()
		.get("/user//suscripcion")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testInvalidUserId() {
		RestAssured.given()
		.get("/user/aa/suscripcion")
		.then()
		.statusCode(400)
		;
	}

}
