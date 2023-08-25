package org.circle8.integration.organizacion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(ApiTestExtension.class)
class OrganizacionGetTest {
	@Test
	void testGetOk() {
		RestAssured.given()
			.get("/organizacion/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("razonSocial", notNullValue())
		;
	}
}
