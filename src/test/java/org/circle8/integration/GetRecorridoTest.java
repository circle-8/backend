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
class GetRecorridoTest {
	@Test void testRecorridoWithoutResiduos() {
		RestAssured.given()
			.get("/recorrido/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("fechaRetiro", equalTo("2023-07-03"))
			.body("puntos", hasSize(0))
		;
	}

	@Test void testRecorridoWithResiduos() {
		RestAssured.given()
			.get("/recorrido/3")
			.then()
			.statusCode(200)
			.body("id", equalTo(3))
			.body("fechaRetiro", equalTo("2023-08-01"))
			.body("puntos", hasSize(2))
			.body("puntos[0].latitud", equalTo(-34.6611203f))
			.body("puntos[0].longitud", equalTo(-58.5422521f))
			.body("puntos[0].residuo.descripcion", equalTo("Residuo en recorrido"))
			.body("puntos[1].latitud", equalTo(-34.66381f))
			.body("puntos[1].longitud", equalTo(-58.581509f))
			.body("puntos[1].residuo.descripcion", equalTo("Residuo en recorrido 2"))
		;
	}
	@Test void testRecorridoExpandZona() {}
	@Test void testRecorridoExpandReciclador() {}
	@Test void testRecorridoNotFound() {}
}
