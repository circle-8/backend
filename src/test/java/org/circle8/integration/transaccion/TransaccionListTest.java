package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(ApiTestExtension.class)
public class TransaccionListTest {

	@Test
	void testListOk() {
		RestAssured.given()
					  .get("/transacciones")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(4))
					  .body("data[0].id", equalTo(1))
		;
	}

	@Test
	void testListTransportistaFilter() {
		RestAssured.given()
					  .get("/transacciones?transportista=1")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(1))
		;
	}

	@Test
	void testListTransportistaFilterTransporteExpand() {
		RestAssured.given()
					  .get("/transacciones?transportista=1&expand=transporte")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(1))
					  .body("data[0].transporte.transportistaId", equalTo(1))
		;
	}

	@Test
	void testListPuntoResiduoFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(3))
					  .body("data[0].puntoReciclajeId", equalTo(1))
					  .body("data[0].id", equalTo(1))
					  .body("data[1].puntoReciclajeId", equalTo(1))
					  .body("data[1].id", equalTo(3))
					  .body("data[2].puntoReciclajeId", equalTo(1))
					  .body("data[2].id", equalTo(4))
		;
	}

	@Test
	void testListPuntosResiduosFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1&punto_reciclaje=2")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(4))
					  .body("data[0].puntoReciclajeId", equalTo(1))
					  .body("data[1].puntoReciclajeId", equalTo(2))
					  .body("data[2].puntoReciclajeId", equalTo(1))
		;
	}



	@Test
	void testListPuntoResiduoAndTransportistaFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1&transportista=1")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(1))
					  .body("data[0].puntoReciclajeId", equalTo(1))
					  .body("data[0].transporteId", equalTo(1))
		;
	}

}
