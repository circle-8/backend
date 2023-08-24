package org.circle8.integration.residuo;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
 class ResiduosListTest {
	@Test void testListWithoutFilter() {
		RestAssured.given()
			.get("/residuos")
			.then()
			.body("data", hasSize(13))
		;
	}

	@Test void testListWithPuntosResiduosFilter() {
		RestAssured.given()
			.get("/residuos?puntos_residuo=1&puntos_residuo=3")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.puntoResiduoId", everyItem(anyOf(equalTo(1), equalTo(3))))
		;
	}

	@Test void testListWithCiudadanosFilter() {
		RestAssured.given()
			.get("/residuos?ciudadanos=1&ciudadanos=3")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.puntoResiduoUri",  everyItem(anyOf(containsString("/ciudadano/1"), containsString("/ciudadano/3"))))
		;
	}

	@Test void testListWithTiposFilter() {
		RestAssured.given()
			.get("/residuos?tipos=1&tipos=3")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.tipoResiduo.id", everyItem(anyOf(equalTo(1), equalTo(3))))
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
			.body("data", hasSize(greaterThan(0)))
			.body("data.fechaRetiro", everyItem(notNullValue()))
		;
	}

	@Test void testListWithRetiradoFalseFilter() {
		RestAssured.given()
			.get("/residuos?retirado=false")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.fechaRetiro", everyItem(nullValue()))
		;
	}

	@Test void testListWithFechaLimiteRetiroFilter() {
		RestAssured.given()
			.get("/residuos?fecha_limite_retiro=2023-08-01T00:00:00.000Z")
			.then()
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test void testListWithEveryFilter() {
		RestAssured.given()
			.get("/residuos?puntos_residuo=1&ciudadanos=1&tipos=1&retirado=false&fecha_limite_retiro=2023-08-01T00:00:00.000Z")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.puntoResiduoId", everyItem(equalTo(1)))
			.body("data.puntoResiduoUri", everyItem(containsString("/ciudadano/1")))
			.body("data.tipoResiduo.id", everyItem(equalTo(1)))
			.body("data.fechaRetiro", everyItem(nullValue()))
		;
	}
}
