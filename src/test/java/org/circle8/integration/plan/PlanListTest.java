package org.circle8.integration.plan;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PlanListTest {

	@Test
	void testList() {
		RestAssured.given()
					  .get("/planes")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(2))
					  .body("data[0].id", equalTo(1))
					  .body("data[0].nombre", equalTo("Free"))
					  .body("data[0].precio", equalTo(0))
					  .body("data[0].mesesRenovacion", equalTo(0))
					  .body("data[0].cantUsuarios", equalTo(0))
					  .body("data[1].id", equalTo(2))
					  .body("data[1].nombre", equalTo("Paid"))
					  .body("data[1].precio", equalTo(1))
					  .body("data[1].mesesRenovacion", equalTo(2))
					  .body("data[1].cantUsuarios", equalTo(3))
		;
	}

}
