package org.circle8.integration.residuo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.containsString;

@ExtendWith(ApiTestExtension.class)
class ResiduoPostReciclajeTest {
	@Test void testResiduoFulfilled() {
		RestAssured.given()
			.post("/residuo/3/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("ya ha sido retirado"))
		;
	}

	@Test void testResiduoWithTransaccion() {
		RestAssured.given()
			.post("/residuo/7/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("transacción"))
		;
	}

	@Test void testResiduoWithRecorrido() {
		RestAssured.given()
			.post("/residuo/6/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("parte de un recorrido"))
		;
	}

	@Test void testResiduoWithPendingSolicitud() {
		RestAssured.given()
			.post("/residuo/1/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("solicitudes pendientes"))
		;
	}

	@Test void testResiduoPuntoResiduoWithoutZona() {
		RestAssured.given()
			.post("/residuo/9/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("no tiene asociada una zona"))
		;
	}

	@Test void testResiduoPuntoResiduoWithZonaWithoutTipoResiduo() {
		RestAssured.given()
			.post("/residuo/10/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("tipo de residuo"))
		;
	}

	@Test void testResiduoWithZonaWithoutRecorrido() {
		RestAssured.given()
			.post("/residuo/11/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("no posee un recorrido pendiente"))
		;
	}

	@Test void testResiduoWithCancelledSolicitudOK() {
		RestAssured.given()
			.post("/residuo/12/reciclaje")
			.then()
			.statusCode(200)
		;
	}
}
