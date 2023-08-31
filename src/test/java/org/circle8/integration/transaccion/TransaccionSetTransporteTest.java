package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionSetTransporteTest {

	@Test
	void testPostOk() {
		RestAssured.given()
					  .post("/transaccion/4/transporte/1")
					  .then()
					  .statusCode(200)
		;
	}

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .post("/transaccion/1a2/transporte/1")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testInvalidTransporteId() {
		RestAssured.given()
					  .post("/transaccion/1/transporte/1a")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .post("/transaccion//transporte/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .post("/transaccion/5/transporte/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWhithInexistingTransporteId() {
		RestAssured.given()
					  .post("/transaccion/1/transporte/6")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testWhithTransaccionThatAlreadyHasTransporte() {
		RestAssured.given()
					  .post("/transaccion/1/transporte/2")
					  .then()
					  .statusCode(400)
		;
	}

}
