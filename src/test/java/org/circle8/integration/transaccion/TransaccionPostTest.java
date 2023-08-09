package org.circle8.integration.transaccion;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionPostTest {

	@Test
	void testInsertOk() {
		RestAssured.given()
					  .post("/transaccion?punto_reciclaje=2&residuo=3&residuo=4")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(4))
					  .body("puntoReciclajeId", equalTo(2))
					  .body("residuos[0].id", equalTo(3))
					  .body("residuos[1].id", equalTo(4));
	}

	@Test
	void testInsertWithoutPuntoReciclaje() {
		RestAssured.given()
					  .post("/transaccion?residuo=3&residuo=4")
					  .then()
					  .statusCode(400);
	}

	@Test
	void testInsertWithoutResiduo() {
		RestAssured.given()
					  .post("/transaccion?punto_reciclaje=4")
					  .then()
					  .statusCode(400);
	}

}
