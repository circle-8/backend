package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(ApiTestExtension.class)
 class ResiduosListTest {
	@Test void testListWithoutFilter() {
		RestAssured.given()
			.get("/residuos")
			.then()
			.body("data", hasSize(4))
			.body("data.id", contains(equalTo(1), equalTo(2), equalTo(3), equalTo(4)))
		;
	}

	@Test void testListWithPuntosResiduosFilter() {
		RestAssured.given()
			.get("/residuos?puntos_residuo=1&puntos_residuo=3")
			.then()
			.body("data", hasSize(4))
			.body("data.id", contains(equalTo(1), equalTo(2), equalTo(3), equalTo(4)))
		;
	}

	@Test void testListWithCiudadanosFilter() {
		RestAssured.given()
			.get("/residuos?ciudadanos=1&ciudadanos=3")
			.then()
			.body("data", hasSize(4))
			.body("data.id", contains(equalTo(1), equalTo(2), equalTo(3), equalTo(4)))
		;
	}

	@Test void testListWithTiposFilter() {
		RestAssured.given()
			.get("/residuos?tipos=1&tipos=3")
			.then()
			.body("data", hasSize(3))
			.body("data.id", contains(equalTo(1), equalTo(2), equalTo(3)))
		;
	}

	@Test void testListWithTransaccionFilter() {
		/* TODO: hacer cuando esten armados los endpoints de transaccion */
	}

	@Test void testListWithRecorridoFilter() {
		/* TODO: hacer cuando esten armados los endpoints de recorrido */
	}

	@Test void testListWithRetiradoTrueFilter() {
		RestAssured.given()
			.get("/residuos?retirado=true")
			.then()
			.body("data", hasSize(1))
			.body("data.id", contains(equalTo(3)))
		;
	}

	@Test void testListWithRetiradoFalseFilter() {
		RestAssured.given()
			.get("/residuos?retirado=false")
			.then()
			.body("data", hasSize(3))
			.body("data.id", contains(equalTo(1), equalTo(2), equalTo(4)))
		;
	}

	@Test void testListWithFechaLimiteRetiroFilter() {
		RestAssured.given()
			.get("/residuos?fecha_limite_retiro=2023-08-01T00:00:00.000Z")
			.then()
			.body("data", hasSize(3))
			.body("data.id", contains(equalTo(1), equalTo(3), equalTo(4)))
		;
	}

	@Test void testListWithEveryFilter() {
		RestAssured.given()
			.get("/residuos?puntos_residuo=1&ciudadanos=1&tipos=1&retirado=false&fecha_limite_retiro=2023-08-01T00:00:00.000Z")
			.then()
			.body("data", hasSize(1))
			.body("data.id", contains(equalTo(1)))
		;
	}
}
