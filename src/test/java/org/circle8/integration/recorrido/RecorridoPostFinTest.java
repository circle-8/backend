package org.circle8.integration.recorrido;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPostFinTest {
	@Test
	void testPostOk() {
		RestAssured.given()
		  .post("/recorrido/1/fin")
		  .then()
		  .statusCode(200)
		  .body("fechaFin", not(nullValue()));
	}

	@Test
	void testUnexistingRecorrido() {
		RestAssured.given()
					  .post("/recorrido/10/fin")
					  .then()
					  .statusCode(404)
		;
	}
}
