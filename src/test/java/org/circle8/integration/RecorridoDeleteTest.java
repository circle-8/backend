package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
class RecorridoDeleteTest {
	@Test
	void testOK() {
		RestAssured.given()
			.delete("/organizacion/1/zona/1/recorrido/1")
			.then()
			.statusCode(200)
		;
	}

	@Test
	void testDeleteWithResiduos() {
		RestAssured.given()
			.delete("/organizacion/1/zona/1/recorrido/3")
			.then()
			.statusCode(400)
			.body("message", Matchers.stringContainsInOrder("recorrido", "residuos", "no puede ser eliminado"))
		;
	}
}
