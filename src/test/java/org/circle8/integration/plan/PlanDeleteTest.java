package org.circle8.integration.plan;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PlanDeleteTest {
	@Test
	void testDeleteOk() {
		RestAssured.given()
					  .delete("/plan/1")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testInvalidId() {
		RestAssured.given()
					  .delete("/plan/asd")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testNotExistingId() {
		RestAssured.given()
					  .delete("/plan/3")
					  .then()
					  .statusCode(404)
		;
	}

}
