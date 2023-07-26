package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;

@ExtendWith(ApiTestExtension.class)
class ResiduoFulfillTest {

	@Test void testFulfillWrongId() {
		RestAssured.given()
			.post("/residuo/aoeu/fulfill")
			.then()
			.statusCode(400)
			.body("message", containsString("id"));
	}

	@Test void testFulfillNotFoundId() {
		RestAssured.given()
			.post("/residuo/1000/fulfill")
			.then()
			.statusCode(404)
			.body("message", containsString("residuo"));
	}

	@Test void testFulfillFulfilledResiduo() {
		RestAssured.given()
			.post("/residuo/3/fulfill")
			.then()
			.statusCode(400)
			.body("message", stringContainsInOrder("residuo", "retirado"));
	}

	@Test void testFulfillOK() {
		RestAssured.given()
			.post("/residuo/1/fulfill")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("fechaRetiro", is(notNullValue()))
			;
	}
}
