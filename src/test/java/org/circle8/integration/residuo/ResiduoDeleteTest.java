package org.circle8.integration.residuo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
class ResiduoDeleteTest {

	@Test 
	void testDeleteOk() {
		RestAssured.given()
			.delete("/residuo/3")
			.then()
			.statusCode(200);
	}

	@Test 
	void testInvalidId() {
		RestAssured.given()
			.delete("/residuo/aaa")
			.then()
			.statusCode(400);
	}

	@Test 
	void testNotFound() {
		RestAssured.given()
			.delete("/residuo/0")
			.then()
			.statusCode(500);
	}

	@Test 
	void testWithSolicitud() {
		RestAssured.given()
			.delete("/residuo/1")
			.then()
			.statusCode(400);
	}
}
