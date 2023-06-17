package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.*;

@ExtendWith(ApiTestExtension.class)
class SignUpTest {

	@Test
	void testNewUser() {
		var request = """
   {
     "username": "nuevo-usuario",
     "password": "1234",
     "nombre": "Nuevo Usuario",
     "email": "nuevo@email.com",
     "tipoUsuario": "CIUDADANO"
   }""";

		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario"))
			.body("nombre", equalTo("Nuevo Usuario"))
			.body("email", equalTo("nuevo@email.com"))
			.body("tipoUsuario", equalTo("CIUDADANO"))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;

		// TODO: ver si efectivamente el usuario esta en DB
	}

	@Test
	void testExistingUsername() {
		var request = """
  {
    "username": "existing",
    "password": "1234",
    "nombre": "Nuevo Usuario",
    "email": "nuevo@email.com",
    "tipoUsuario": "CIUDADANO"
  }""";
		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
			.body("message", stringContainsInOrder("usuario", "registrado"))
			;
	}

	@Test
	void testExistingEmail() {
		var request = """
  {
    "username": "nuevo",
    "password": "1234",
    "nombre": "Nuevo Usuario",
    "email": "existing@email.com",
    "tipoUsuario": "CIUDADANO"
  }""";
		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
			.body("message", stringContainsInOrder("email", "registrado"))
		;
	}
}
