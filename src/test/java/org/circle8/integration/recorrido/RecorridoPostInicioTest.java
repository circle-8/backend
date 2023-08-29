package org.circle8.integration.recorrido;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPostInicioTest {

	@Test
	void testPostOk() {
		RestAssured.given()
					  .post("/recorrido/1/inicio")
					  .then()
					  .statusCode(200)
					  .body("fechaInicio", not(nullValue()));
		;
	}


	@Test
	void testUnexistingRecorrido() {
		RestAssured.given()
					  .post("/recorrido/10/inicio")
					  .then()
					  .statusCode(404)
		;
	}

}
