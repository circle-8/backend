package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
class TransaccionListTest {

	@Test
	void testListOk() {
		RestAssured.given()
					  .get("/transacciones")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(greaterThan(1)))
					  .body("data.fechaRetiro", everyItem(is(nullValue())))
		;
	}

	@Test
	void testListTransportistaFilterTransporteExpand() {
		RestAssured.given()
					  .get("/transacciones?transportista_id=1&expand=transporte")
					  .then()
					  .statusCode(200)
					  .body("data.transporte.transportistaId", everyItem(equalTo(1)))
		;
	}

	@Test
	void testListPuntoResiduoFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1")
					  .then()
					  .statusCode(200)
					  .body("data.puntoReciclajeId", everyItem(equalTo(1)))
		;
	}

	@Test
	void testListPuntosResiduosFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1&punto_reciclaje=2")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(greaterThan(1)))
					  .body("data.puntoReciclajeId", everyItem(anyOf(equalTo(1), equalTo(2))))
		;
	}
	
	@Test
	void testListConTransporteFilterTrue() {
		RestAssured.given()
					  .get("/transacciones?con_transporte=true")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void testListConTransporteFilterFalse() {
		RestAssured.given()
					  .get("/transacciones?con_transporte=false")
					  .then()
					  .statusCode(200)
					  .body("data", hasSize(1))
		;
	}


	@Test
	void testListPuntoResiduoAndTransportistaFilter() {
		RestAssured.given()
					  .get("/transacciones?punto_reciclaje=1&transportista_id=1")
					  .then()
					  .statusCode(200)
					  .body("data.puntoReciclajeId", everyItem(equalTo(1)))
					  .body("data.transporteId", everyItem(equalTo(1)))
		;
	}

}
