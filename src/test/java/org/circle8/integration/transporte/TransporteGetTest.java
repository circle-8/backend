package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransporteGetTest {

	@Test
	void testGetOk() {
		RestAssured.given()
			.get("/transporte/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("precioAcordado", equalTo(40.0F))
			.body("fechaAcordada", is(nullValue()))
			.body("fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("pagoConfirmado", equalTo(false))
			.body("entregaConfirmada", equalTo(false))
			.body("precioSugerido", equalTo(2500.0F))			
		;
	}

	@Test
	void testGetOkWithExpandTransportista() {
		RestAssured.given()
			.get("/transporte/1?expand=transportista")
			.then()
			.statusCode(200)
			.body("transportistaUri", equalTo("/user/1"))
			.body("transportistaId", equalTo(1))
			.body("transportista", notNullValue())
			.body("transportista.id", equalTo(1))
			.body("transportista.polylineAlcance", is(not(hasSize(0))))
		;
	}	
	
	@Test
	void testGetOkWithExpandTransaccion() {
		RestAssured.given()
			.get("/transporte/1?expand=transaccion")
			.then()
			.statusCode(200)
			.body("transaccionId", equalTo(1))			
			.body("transaccionUri", equalTo("/transaccion/1"))
		;
	}	

	@Test
	void testNotFound() {
		RestAssured.given()
		.get("/transporte/0")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutId() {
		RestAssured.given()
		.get("/transporte/")
		.then()
		.statusCode(404)
		;
	}


	@Test
	void testInvalidId() {
		RestAssured.given()
		.get("/transporte/aa")
		.then()
		.statusCode(400)
		;
	}

}
