package org.circle8.integration.punto_residuo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
class PuntoResiduoGetTest {
	@Test
	void testBasicGet() {
		RestAssured.given()
			.get("/ciudadano/1/punto_residuo/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("latitud", equalTo(-34.6611203f))
			.body("longitud", equalTo(-58.5422521f))
			.body("ciudadanoId", equalTo(1))
			.body("ciudadanoUri", equalTo("/user/1"))
			.body("ciudadano", not(nullValue()))
			.body("ciudadano.id", equalTo(1))
		;
	}

	@Test
	void testGetNotFoundByCiudadanoId() {
		RestAssured.given()
			.get("/ciudadano/2/punto_residuo/1")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testGetNotFoundById() {
		RestAssured.given()
			.get("/ciudadano/1/punto_residuo/2")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testGetExpandCiudadano() {
		RestAssured.given()
			.get("/ciudadano/1/punto_residuo/1?expand=ciudadano")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("ciudadano", not(nullValue()))
			.body("ciudadano.id", equalTo(1))
			.body("ciudadano.nombre", equalTo("Usuario Existente"))
			.body("ciudadano.email", equalTo("existing@email.com"))
			.body("ciudadano.tipoUsuario", equalTo("CIUDADANO"))
		;
	}

	@Test
	void testGetExpandResiduos() {
		RestAssured.given()
			.get("/ciudadano/1/punto_residuo/1?expand=residuos")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("ciudadano", not(nullValue()))
			.body("residuos", hasSize(greaterThan(1)))
			.body("residuos.id", everyItem(notNullValue()))
			.body("residuos.fechaCreacion", everyItem(notNullValue()))
			.body("residuos.puntoResiduoId", everyItem(notNullValue()))
		;
	}

	@Test
	void testGetExpandResiduosAndCiudadano() {
		RestAssured.given()
			.get("/ciudadano/1/punto_residuo/1?expand=residuos&expand=ciudadano")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("ciudadano", not(nullValue()))
			.body("ciudadano.id", equalTo(1))
			.body("ciudadano.nombre", equalTo("Usuario Existente"))
			.body("ciudadano.email", equalTo("existing@email.com"))
			.body("ciudadano.tipoUsuario", equalTo("CIUDADANO"))
			.body("residuos", hasSize(greaterThan(1)))
			.body("residuos.id", everyItem(notNullValue()))
			.body("residuos.fechaCreacion", everyItem(notNullValue()))
			.body("residuos.puntoResiduoId", everyItem(notNullValue()))
		;
	}
}
