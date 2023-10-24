package org.circle8.integration.transporte;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
public class TransporteListTest {

	@Test
	void ListWithoutFilterExpands() {
		RestAssured.given()
			.get("/transportes")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void ListWithoutFilterWithExpandTransportista() {
		RestAssured.given()
			.get("/transportes?expand=transportista")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void ListWithoutFilterWithExpandTransaccion() {
		RestAssured.given()
			.get("/transportes?expand=transaccion")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void ListWithoutFilterWithExpands() {
		RestAssured.given()
			.get("/transportes?expand=transaccion&expand=transportista")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void ListWithTransportistaFilter() {
		RestAssured.given()
			.get("/transportes?transportista_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(1))
			.body("data[0].precioAcordado", equalTo(40))
			.body("data[0].fechaAcordada", is(nullValue()))
			.body("data[0].fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("data[0].fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("data[0].pagoConfirmado", equalTo(false))
			.body("data[0].entregaConfirmada", equalTo(false))
			.body("data[0].precioSugerido", equalTo(2500))
		;
	}

	@Test
	void ListWithNotFoundTransportistaFilter() {
		RestAssured.given()
			.get("/transportes?transportista_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void ListWithInvalidTransportistaFilter() {
		RestAssured.given()
			.get("/transportes?transportista_id=aa")
			.then()
			.statusCode(400)
		;
	}


	@Test
	void ListWithUserFilter() {
		RestAssured.given()
			.get("/transportes?user_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(1))
			.body("data[0].precioAcordado", equalTo(40))
			.body("data[0].fechaAcordada", is(nullValue()))
			.body("data[0].fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("data[0].fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("data[0].pagoConfirmado", equalTo(false))
			.body("data[0].entregaConfirmada", equalTo(false))
			.body("data[0].precioSugerido", equalTo(2500))
		;
	}

	@Test
	void ListWithNotFoundUserFilter() {
		RestAssured.given()
			.get("/transportes?user_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void ListWithInvalidUserFilter() {
		RestAssured.given()
			.get("/transportes?user_id=aa")
			.then()
			.statusCode(400)
		;
	}


	@Test
	void ListWithEntregaConfirmadaTrueFilter() {
		RestAssured.given()
			.get("/transportes?entrega_confirmada=true")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(2))
			.body("data[0].entregaConfirmada", equalTo(true))
		;
	}

	@Test
	void ListWithEntregaConfirmadaFalseFilter() {
		RestAssured.given()
			.get("/transportes?entrega_confirmada=false")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].entregaConfirmada", equalTo(false))
		;
	}

	@Test
	void ListWithPagoConfirmadoTrueFilter() {
		RestAssured.given()
			.get("/transportes?pago_confirmado=true")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].pagoConfirmado", equalTo(true))
		;
	}

	@Test
	void ListWithPagoConfirmadoFalseFilter() {
		RestAssured.given()
			.get("/transportes?pago_confirmado=false")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(1))
			.body("data[0].pagoConfirmado", equalTo(false))
		;
	}


	@Test
	void ListWithSoloSinTransportistaTrueFilter() {
		RestAssured.given()
			.get("/transportes?solo_sin_transportista=true")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].transportistaId", is(nullValue()))
		;
	}

	@Test
	void ListWithSoloSinTransportistaFalseFilter() {
		RestAssured.given()
			.get("/transportes?solo_sin_transportista=false")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
		;
	}

	@Test
	void ListWithFechaRetiroFilter() {
		RestAssured.given()
			.get("/transportes?fecha_retiro=2020-03-02")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(3))
			.body("data[0].fechaAcordada", equalTo("2020-03-02"))
		;
	}

	@Test
	void ListWithInvalidFechaRetiroFilter() {
		RestAssured.given()
			.get("/transportes?fecha_retiro=aaa")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void ListWithNotFoundFechaRetiroFilter() {
		RestAssured.given()
			.get("/transportes?fecha_retiro=2023-08-02")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}


	@Test
	void ListWithTransaccionFilter() {
		RestAssured.given()
			.get("/transportes?transaccion_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(1))
		;


		RestAssured.given()
			.get("/transportes?transaccion_id=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(2))
		;

		RestAssured.given()
			.get("/transportes?transaccion_id=3")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(0)))
			.body("data[0].id", equalTo(2))
		;

		RestAssured.given()
			.get("/transportes?transaccion_id=4")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void ListWithNotFoundTransaccionFilter() {
		RestAssured.given()
			.get("/transportes?transaccion_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void ListWithInvalidTransaccionFilter() {
		RestAssured.given()
			.get("/transportes?transaccion_id=aa")
			.then()
			.statusCode(400)
		;
	}


}
