package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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
			.body("residuos", hasSize(1))
			.body("residuos[0].id", equalTo(1))
			.body("residuos[0].fechaCreacion", equalTo("2023-07-02T16:41"))
			.body("residuos[0].puntoResiduoUri", equalTo("/ciudadano/1/punto_residuo/1"))
			.body("residuos[0].puntoResiduoId", equalTo(1))
			.body("residuos[0].puntoResiduo", not(nullValue()))
			.body("residuos[0].puntoResiduo.id", equalTo(1))
			.body("residuos[0].tipoResiduo.id", equalTo(1))
			.body("residuos[0].tipoResiduo.nombre", equalTo("Plástico"))
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
			.body("residuos", hasSize(1))
			.body("residuos[0].id", equalTo(1))
			.body("residuos[0].fechaCreacion", equalTo("2023-07-02T16:41"))
			.body("residuos[0].puntoResiduoUri", equalTo("/ciudadano/1/punto_residuo/1"))
			.body("residuos[0].puntoResiduoId", equalTo(1))
			.body("residuos[0].puntoResiduo", not(nullValue()))
			.body("residuos[0].puntoResiduo.id", equalTo(1))
			.body("residuos[0].tipoResiduo.id", equalTo(1))
			.body("residuos[0].tipoResiduo.nombre", equalTo("Plástico"))
		;
	}
}
