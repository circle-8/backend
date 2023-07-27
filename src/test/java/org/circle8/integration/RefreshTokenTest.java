package org.circle8.integration;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.circle8.ApiTestExtension;
import org.circle8.controller.response.TokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
class RefreshTokenTest {

	@Test
	void testValidRefresh() {
		var loginRequest = """
   {
     "username": "existing",
     "password": "1234"
   }""";

		var token = RestAssured.given().body(loginRequest).post("/token").getBody().as(TokenResponse.class);
		String access = token.token;
		String refresh = token.refreshToken;

		var req = String.format("""
   {
   	"accessToken": %s,
   	"refreshToken": %s
   }""", access, refresh);

		RestAssured.given()
			.body(req)
			.post("/refresh_token")
			.then()
			.statusCode(200)
			.body("token", is(not(empty())))
			.body("refreshToken", is(not(empty())));
	}

	@Test
	void testInvalidRefresh() {
		var loginRequest = """
   {
     "username": "existing",
     "password": "1234"
   }""";

		var token = RestAssured.given().body(loginRequest).post("/token").getBody().as(TokenResponse.class);
		String access = token.token;
		String refresh = "invalid";

		var req = String.format("""
   {
   	"accessToken": %s,
   	"refreshToken": %s
   }""", access, refresh);

		RestAssured.given()
			.body(req)
			.post("/refresh_token")
			.then()
			.statusCode(400);
	}

	@Test
	void testInvalidToken() {
		var loginRequest = """
   {
     "username": "existing",
     "password": "1234"
   }""";

		var token = RestAssured.given().body(loginRequest).post("/token").getBody().as(TokenResponse.class);
		String access = "invalid";
		String refresh = token.refreshToken;

		var req = String.format("""
   {
     "accessToken": %s,
     "refreshToken": %s
   }""", access, refresh);

		RestAssured.given()
			.body(req)
			.post("/refresh_token")
			.then()
			.statusCode(400);
	}
}
