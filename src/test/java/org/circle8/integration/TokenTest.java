package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;

@ExtendWith(ApiTestExtension.class)
class TokenTest {

	@Test
	void testValidCiudadano() {
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
			.body("user.tipoUsuario", equalTo("CIUDADANO"))
			.body("user.ciudadanoId", equalTo(1))
			.body("token", is(not(empty())))
			.body("refreshToken", is(not(empty())))
			;
	}
	
	@Test
	void testValidReciclador() {
		var request = """
   {
     "username": "reciclador1",
     "password": "1234"
   }""";

		RestAssured.given()
			.body(request)
			.post("/token")
			.then()
			.statusCode(200)
			.body("user.id", equalTo(3))
			.body("user.username", equalTo("reciclador1"))
			.body("user.tipoUsuario", equalTo("RECICLADOR_URBANO"))
			.body("user.ciudadanoId", nullValue())
			.body("user.recicladorUrbanoId", equalTo(1))
			.body("user.organizacionId", equalTo(1))
			.body("user.zonaId", equalTo(1))
			.body("token", is(not(empty())))
			.body("refreshToken", is(not(empty())))
			;
	}
	
	@Test
	void testValidRecicladorWithOutZona() {
		var request = """
   {
     "username": "reciclador2",
     "password": "1234"
   }""";

		RestAssured.given()
			.body(request)
			.post("/token")
			.then()
			.statusCode(200)
			.body("user.id", equalTo(4))
			.body("user.username", equalTo("reciclador2"))
			.body("user.tipoUsuario", equalTo("RECICLADOR_URBANO"))
			.body("user.ciudadanoId", nullValue())
			.body("user.recicladorUrbanoId", equalTo(2))
			.body("user.organizacionId", equalTo(2))
			.body("user.zonaId", nullValue())
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
