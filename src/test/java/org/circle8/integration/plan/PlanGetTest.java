package org.circle8.integration.plan;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PlanGetTest {


	@Test
	void testGetOkThenReturnPlan() {
		RestAssured.given()
					  .get("/plan/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
		;
	}

	@Test
	void testGetParamErrorThenReturnBadRequest() {
		RestAssured.given()
					  .get("/plan/e")
					  .then()
					  .statusCode(400)
					  .body("code", equalTo("BAD_REQUEST"))
		;
	}

	@Test
	void testGetIdNotExistThenReturnNotFound() {
		RestAssured.given()
					  .get("/plan/3")
					  .then()
					  .statusCode(404)
					  .body("code", equalTo("NOT_FOUND"))
		;
	}
}
