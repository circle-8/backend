package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(ApiTestExtension.class)
class TransaccionPutResiduoTest {

	@Test
	void testPutSinSolicitud() {
		RestAssured.given()
					  .put("/transaccion/2/residuo/1")
					  .then()
					  .statusCode(404);
	}

	@Test
	void testPutOk() {
		RestAssured.given()
			.put("/transaccion/2/residuo/12")
			.then()
			.statusCode(200)
			.body("id", equalTo(2))
			.body("residuos[0].id", equalTo(12));
	}

	@Test
	void testTransaccionNotExist() {
		RestAssured.given()
					  .put("/transaccion/0/residuo/1")
					  .then()
					  .statusCode(404);
	}

	@Test
	void testResiduoNotExist() {
		RestAssured.given()
					  .put("/transaccion/2/residuo/50")
					  .then()
					  .statusCode(404);
	}

	@Test
	void testTransaccionIdWrong() {
		RestAssured.given()
					  .put("/transaccion/5a/residuo/1")
					  .then()
					  .statusCode(400);
	}

	@Test
	void testResiduoIdWrong() {
		RestAssured.given()
					  .put("/transaccion/1/residuo/1a")
					  .then()
					  .statusCode(400);
	}

}
