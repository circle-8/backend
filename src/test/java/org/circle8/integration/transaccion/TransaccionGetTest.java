package org.circle8.integration.transaccion;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionGetTest {

	@Test
	void testGetOkThenReturnTransaccion() {
		RestAssured.given()
					  .get("/transaccion/1")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testGetParamErrorThenReturnBadRequest() {
		RestAssured.given()
					  .get("/transaccion/2e")
					  .then()
					  .statusCode(400)
					  .body("code", equalTo("BAD_REQUEST"))
		;
	}

	@Test
	void testGetIdNotExistThenReturnNotFound() {
		RestAssured.given()
					  .get("/transaccion/9999")
					  .then()
					  .statusCode(404)
					  .body("code", equalTo("NOT_FOUND"))
		;
	}

}
