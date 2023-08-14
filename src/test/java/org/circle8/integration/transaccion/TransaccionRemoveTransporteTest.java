package org.circle8.integration.transaccion;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionRemoveTransporteTest {

	@Test
	void testDeleteOk() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/1")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1a2/transporte/1")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testInvalidTransporteId() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/1a")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion//transporte/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhitoutTransporteId() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/5/transporte/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransporteId() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/6")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithTransporteIdFromAnotherTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/2")
					  .then()
					  .statusCode(404)
		;
	}

}
