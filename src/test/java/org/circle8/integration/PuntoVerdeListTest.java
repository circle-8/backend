package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
class PuntoVerdeListTest {

	@Test
	void testListWithoutFilter() {
		RestAssured.given()
			.get("/puntos_verdes")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].id", equalTo(3))
			.body("data[0].latitud", equalTo(-34.6519877f))
			.body("data[0].longitud", equalTo(-58.5850894f))
			.body("data[0].dias", hasSize(4))
			.body("data[0].dias", hasItems("0", "1", "3", "4"))
			.body("data[0].recicladorId", nullValue())
			.body("data[0].recicladorUri", nullValue())
			.body("data[0].titulo", equalTo("Punto VERDE 1"))
			.body("data[1].id", equalTo(4))
			.body("data[1].latitud", equalTo(-34.6707576f))
			.body("data[1].longitud", equalTo(-58.5628052f))
			.body("data[1].dias", hasSize(3))
			.body("data[1].dias", hasItems("0", "1", "3"))
			.body("data[1].recicladorId", nullValue())
			.body("data[1].recicladorUri", nullValue())
			.body("data[1].titulo", equalTo("Punto VERDE 2"))
		;
	}

	@Test
	void testListWithDiasFilter() {
		RestAssured.given()
			.get("/puntos_verdes?dias=4&dias=5&dias=6") // Viernes, Sábado, Domingo
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].id", equalTo(3))
		;
	}

	@Test
	void testListWithTiposFilter() {
		RestAssured.given()
			.get("/puntos_verdes?tipos_residuo=Pilas&tipos_residuo=Orgánico")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].id", equalTo(4))
			.body("data[0].tipoResiduo", hasSize(2))
			.body("data[0].tipoResiduo.find{it.id == 3}.nombre", equalTo("Pilas"))
			.body("data[0].tipoResiduo.find{it.id == 4}.nombre", equalTo("Carton"))
		;
	}

	@Test
	void testListWithAreaFilter() {
		RestAssured.given()
			.get("/puntos_verdes?latitud=-34.65&longitud=-58.58&radio=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].id", equalTo(3))
			.body("data[1].id", equalTo(4))
		;
	}

	@Test
	void testListWithAllFilters() {
		RestAssured.given()
			.get("/puntos_verdes?latitud=-34.65&longitud=-58.58&radio=1&tipos_residuo=Pilas&tipos_residuo=Orgánico&dias=3")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].id", equalTo(4))
		;
	}
}
