package org.circle8.integration.suscripcion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class SuscripcionListTest {

	@Test
	void testListOk() {
		RestAssured.given()
			.get("/suscripciones")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
			.body("data[0].ultimaRenovacion", equalTo("2023-09-21"))
			.body("data[0].proximaRenovacion", equalTo("2023-09-21"))
			.body("data[0].plan.id", equalTo(1))
			.body("data[0].plan.nombre", equalTo("Free"))
			.body("data[0].plan.precio", equalTo(0))
			.body("data[0].plan.mesesRenovacion", equalTo(12))
			.body("data[0].plan.cantidadUsuarios", equalTo(3))	
			.body("data[1].id", equalTo(2))
			.body("data[1].ultimaRenovacion", equalTo("2023-10-21"))
			.body("data[1].proximaRenovacion", equalTo("2023-10-21"))
			.body("data[1].plan.id", equalTo(1))
			.body("data[1].plan.nombre", equalTo("Free"))
			.body("data[1].plan.precio", equalTo(0))
			.body("data[1].plan.mesesRenovacion", equalTo(12))
			.body("data[1].plan.cantidadUsuarios", equalTo(3))	
		;
	}	

	@Test
	void testWithFilterId() {
		RestAssured.given()
			.get("/suscripciones?id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].id", equalTo(1))
		;
	}	

	@Test
	void testWithFilterPlanId() {
		RestAssured.given()
			.get("/suscripciones?plan_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].plan.id", equalTo(1))
			.body("data[1].plan.id", equalTo(1))
		;
	}
}
