package org.circle8.integration.plan;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PlanPostTest {

	private static final String BODY =  """
		{
		 	"nombre" : "Fremium",
		 	"precio" : 3,
		 	"cantUsuarios" : 2,
		 	"mesesRenovacion" : 5
		}""";

	private static final String BODY_UNCOMPLETED =  """
		{
		 	"nombre" : "Fremium",
		 	"precio" : 3,
		 	"mesesRenovacion" : 5
		}""";

	@Test
	void testPutOk() {
		RestAssured.given()
					  .body(BODY)
					  .post("/plan")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testWithOutBody() {
		RestAssured.given()
					  .post("/plan")
					  .then()
					  .statusCode(500)
		;
	}
	@Test
	void testWithBodyUncompleted() {
		RestAssured.given()
					  .body(BODY_UNCOMPLETED)
					  .post("/plan")
					  .then()
					  .statusCode(400)
		;
	}
}
