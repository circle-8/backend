package org.circle8.integration.recorrido;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

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
	void testFinWithResiduosUnfulfilled() {
		RestAssured.given()
			.post("/recorrido/3/fin")
			.then()
			.statusCode(400)
		;
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
