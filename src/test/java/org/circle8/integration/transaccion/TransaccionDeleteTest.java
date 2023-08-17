package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionDeleteTest {

	@Test
	void testDeleteOk() {
		RestAssured.given()
					  .delete("/transaccion/2")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1a2")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/5")
					  .then()
					  .statusCode(404)
		;
	}
}
