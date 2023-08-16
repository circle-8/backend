package org.circle8.integration.transaccion;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionPutResiduoTest {

	@Test
	void testPutOk() {
		RestAssured.given()
					  .put("/transaccion/2/residuo/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(2))
					  .body("residuos[0].id", equalTo(1));
	}

	@Test
	void testTransaccionNotExist() {
		RestAssured.given()
					  .put("/transaccion/5/residuo/1")
					  .then()
					  .statusCode(500);
	}

	@Test
	void testResiduoNotExist() {
		RestAssured.given()
					  .put("/transaccion/2/residuo/50")
					  .then()
					  .statusCode(500);
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
