package org.circle8.integration.transaccion;

import io.restassured.RestAssured;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.stringContainsInOrder;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionRemoveTransporteTest {

	@Test
	void testDeleteOk() {
		RestAssured.given()
					  .delete("/transaccion/5/transporte/")
					  .then()
					  .statusCode(200)
		;
	}
	
	@Test
	void testWithOutTransporte() {
		RestAssured.given()
					  .delete("/transaccion/4/transporte/")
					  .then()
					  .statusCode(404)
		;
	}
	
	@Test
	void testWithTransportista() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/")
					  .then()
					  .statusCode(400)
					  .body("code", equalTo("BAD_REQUEST"))
					  .body("message", stringContainsInOrder("transporte", "transportista"))
		;
	}
	

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1a2/transporte/")
					  .then()
					  .statusCode(400)
		;
	}
	

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion//transporte/")
					  .then()
					  .statusCode(404)
		;
	}

	
	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/0/transporte/")
					  .then()
					  .statusCode(404)
		;
	}
}
