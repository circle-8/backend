package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionRemoveResiduoTest {


	@Test
	void testDeleteOk() {
		RestAssured.given()
					  .delete("/transaccion/1/residuo/7")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1a2/residuo/1")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testInvalidResiduoId() {
		RestAssured.given()
					  .delete("/transaccion/1/residuo/1a")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion//residuo/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhitoutResiduoId() {
		RestAssured.given()
					  .delete("/transaccion/1/residuo/")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/5/residuo/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingResiduoId() {
		RestAssured.given()
					  .delete("/transaccion/1/residuo/10")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithResiduoIdFromAnotherTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/2/residuo/1")
					  .then()
					  .statusCode(404)
		;
	}
}
