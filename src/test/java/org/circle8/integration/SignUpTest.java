package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApiTestExtension.class)
class SignUpTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();

	@Test
	void testNewUser() throws Exception {
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

		var checkUserSQL = "SELECT \"Username\" FROM public.\"Usuario\" WHERE \"Username\" = ? AND \"Email\" = ?";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkUserSQL) ) {
			ps.setString(1, "nuevo-usuario");
			ps.setString(2, "nuevo@email.com");
			assertTrue(ps.executeQuery().next());
		}
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
