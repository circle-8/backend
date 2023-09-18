package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import lombok.val;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(ApiTestExtension.class)
class TransaccionPostTest {

	@Test
	void testInsertOk() {
		val body = """
		{
			"puntoReciclaje": 2,
			"residuoId": [3, 4]
		}
		""";
		RestAssured.given()
			.body(body)
			.post("/transaccion")
			.then()
			.statusCode(200)
			.body("id", equalTo(6))
			.body("puntoReciclajeId", equalTo(2))
			.body("residuos[0].id", equalTo(3))
			.body("residuos[1].id", equalTo(4));
	}

	@Test
	void testInsertWithoutPuntoReciclaje() {
		val body = """
		{
			"residuoId": [3, 4]
		}
		""";
		RestAssured.given()
			.body(body)
			.post("/transaccion")
			.then()
			.statusCode(400);
	}

	@Test
	void testInsertWithoutResiduo() {
		val body = """
		{
			"puntoReciclaje": 4
		}
		""";
		RestAssured.given()
			.body(body)
			.post("/transaccion?punto_reciclaje=4")
			.then()
			.statusCode(400);
	}

}
