package org.circle8.integration.residuo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(ApiTestExtension.class)
class ResiduoGetTest {
	@Test
	void testListWithoutFilter() {
		RestAssured.given()
			.get("/residuo/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
		;
	}
}
