package org.circle8.integration.recorrido;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.circle8.utils.Dates;
import org.exparity.hamcrest.date.DateMatchers;
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
					  .statusCode(200);
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
