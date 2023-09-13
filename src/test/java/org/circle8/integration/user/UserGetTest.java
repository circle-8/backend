package org.circle8.integration.user;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
public class UserGetTest {

	@Test
	void testGetOk() throws Exception {
		RestAssured.given()
			.get("/user/1")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("existing"))
			.body("nombre", equalTo("Usuario Existente"))
			.body("email", is(notNullValue()))
			.body("tipoUsuario", equalTo("CIUDADANO"))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;
	}

	@Test
	void testNotFound() throws Exception {
		RestAssured.given()
			.get("/user/0")
			.then()
			.statusCode(404)
			;
	}

	@Test
	void testWithOutId() throws Exception {
		RestAssured.given()
			.get("/user/")
			.then()
			.statusCode(404)
			;
	}

	@Test
	void testInvalidId() throws Exception {
		RestAssured.given()
			.get("/user/a")
			.then()
			.statusCode(400)
			;
	}
}
