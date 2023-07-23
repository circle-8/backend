package org.circle8.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TipoResiduoListTest {

	@Test
	void testListOk() {
		RestAssured.given()
		.get("/tipos_residuo")
		.then()
		.statusCode(200)
		.body("data", hasSize(4))
		.body("data[0].id", equalTo(1))
		.body("data[0].nombre", equalTo("Plástico"))
	;
	}
}
