package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.stringContainsInOrder;

@ExtendWith(ApiTestExtension.class)
class TokenTest {

	@Test
	void testValidUser() {
		var request = """
   {
     "username": "existing",
     "password": "1234"
   }""";

		RestAssured.given()
			.body(request)
			.post("/token")
			.then()
			.statusCode(200)
			.body("user.id", equalTo(1))
			.body("user.username", equalTo("existing"))
			.body("token", is(not(empty())))
			.body("refreshToken", is(not(empty())))
			;
	}

	@Test
	void testIncorrectPassword() {
		var request = """
   {
     "username": "existing",
     "password": "12345"
   }""";

		RestAssured.given()
			.body(request)
			.post("/token")
			.then()
			.statusCode(404)
			.body("message", stringContainsInOrder("usuario", "contraseña", "incorrectos"))
		;
	}

	@Test
	void testNotFoundUser() {
		var request = """
   {
     "username": "nuevo",
     "password": "12345"
   }""";

		RestAssured.given()
			.body(request)
			.post("/token")
			.then()
			.statusCode(404)
			.body("message", stringContainsInOrder("usuario", "contraseña", "incorrectos"))
		;
	}
}
