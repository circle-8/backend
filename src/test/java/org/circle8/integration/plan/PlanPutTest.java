package org.circle8.integration.plan;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PlanPutTest {

	private static final String BODY =  """
		{
		 	"nombre" : "Fremium",
		 	"precio" : 3,
		 	"cantUsuarios" : 2,
		 	"mesesRenovacion" : 5
		}""";

	@Test
	void testPutOk() {
		RestAssured.given()
					  .body(BODY)
					  .put("/plan/1")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testPutNotFoundId() {
		RestAssured.given()
					  .body(BODY)
					  .put("/plan/5")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWithOutParams() {
		RestAssured.given()
					  .put("/plan/1")
					  .then()
					  .statusCode(500)
		;
	}
}
