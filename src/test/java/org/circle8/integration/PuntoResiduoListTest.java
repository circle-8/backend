package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(ApiTestExtension.class)
class PuntoResiduoListTest {
	@Test
	void testListWithoutFilter() {
		RestAssured.given()
			.get("/puntos_residuo")
			.then()
			.statusCode(200)
			.body("data", is(not(hasSize(0))))
			.body("data[0].id", equalTo(1))
			.body("data[0].latitud", equalTo(-34.66112f))
			.body("data[0].longitud", equalTo(-58.54225f))
			.body("data[0].ciudadanoId", equalTo(1))
			.body("data[0].ciudadanoUri", equalTo("/user/1"))
			.body("data[0].ciudadano", notNullValue())
			.body("data[0].ciudadano.id", equalTo(1))
		;
	}

	@Test
	void testListWithFilter() {
		RestAssured.given()
			.get("/puntos_residuo?latitud=-34.6610&longitud=-58.5420&radio=0.01")
			.then()
			.statusCode(200)
			.body("data", is(not(hasSize(0))))
			.body("data[0].id", equalTo(1))
			.body("data[0].latitud", equalTo(-34.66112f))
			.body("data[0].longitud", equalTo(-58.54225f))
		;
	}

	@Test
	void testListWithResiduosFilter() {
		RestAssured.given()
			.get("/puntos_residuo?tipos_residuo=Plástico&tipos_residuo=Papel")
			.then()
			.statusCode(200)
			.body("data", is(not(hasSize(0))))
			.body("data[0].id", equalTo(1))
			.body("data[0].latitud", equalTo(-34.66112f))
			.body("data[0].longitud", equalTo(-58.54225f))
		;
	}

	@Test
	void testListWithCiudadanoFilter() {
		RestAssured.given()
			.get("/puntos_residuo?ciudadano_id=1")
			.then()
			.statusCode(200)
			.body("data", is(not(hasSize(0))))
			.body("data[0].id", equalTo(1))
			.body("data[0].latitud", equalTo(-34.66112f))
			.body("data[0].longitud", equalTo(-58.54225f))
		;
	}

	@Test
	void testListWithNonMatchingFilter() {
		RestAssured.given()
			.get("/puntos_residuo?latitud=-34.6610&longitud=-58.5420&radio=0.000001")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void testListWithExpand() {
		RestAssured.given()
			.get("/puntos_residuo?expand=ciudadano")
			.then()
			.statusCode(200)
			.body("data", is(not(hasSize(0))))
			.body("data[0].id", equalTo(1))
			.body("data[0].ciudadanoId", equalTo(1))
			.body("data[0].ciudadanoUri", equalTo("/user/1"))
			.body("data[0].ciudadano", notNullValue())
			.body("data[0].ciudadano.id", equalTo(1))
			.body("data[0].ciudadano.username", equalTo("existing"))
			.body("data[0].ciudadano.nombre", equalTo("Usuario Existente"))
			.body("data[0].ciudadano.email", equalTo("existing@email.com"))
			.body("data[0].ciudadano.tipoUsuario", equalTo("CIUDADANO"))
		;
	}

	@Test
	void testListWithExpandAndFilter() {
		RestAssured.given()
			.get("/puntos_residuo?latitud=-34.6610&longitud=-58.5420&radio=0.01&expand=ciudadano")
			.then()
			.statusCode(200)
			.body("data[0].id", equalTo(1))
			.body("data[0].latitud", equalTo(-34.66112f))
			.body("data[0].longitud", equalTo(-58.54225f))
			.body("data[0].ciudadanoId", equalTo(1))
			.body("data[0].ciudadanoUri", equalTo("/user/1"))
			.body("data[0].ciudadano", notNullValue())
			.body("data[0].ciudadano.id", equalTo(1))
			.body("data[0].ciudadano.username", equalTo("existing"))
			.body("data[0].ciudadano.nombre", equalTo("Usuario Existente"))
			.body("data[0].ciudadano.email", equalTo("existing@email.com"))
			.body("data[0].ciudadano.tipoUsuario", equalTo("CIUDADANO"))
		;
	}
}
