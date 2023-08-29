package org.circle8.integration.recorrido;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.time.ZonedDateTime;
import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.circle8.utils.Dates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import lombok.val;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPostFinTest {
	@Test
	void testPostOk() {
		val time = ZonedDateTime.now(Dates.UTC).toInstant();
		val response =RestAssured.given()
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
