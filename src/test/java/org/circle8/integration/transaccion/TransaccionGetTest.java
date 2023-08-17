package org.circle8.integration.transaccion;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(ApiTestExtension.class)
public class TransaccionGetTest {

	@Test
	void testGetOkThenReturnTransaccion() {
		RestAssured.given()
					  .get("/transaccion/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaCreacion", equalTo("2023-01-01T08:00:00Z"))
					  .body("fechaRetiro", equalTo("2023-01-02T08:00:00Z"))
					  .body("transporteId", equalTo(1))
					  .body("puntoReciclajeId", equalTo(1));
		;
	}

	@Test
	void testGetParamErrorThenReturnBadRequest() {
		RestAssured.given()
					  .get("/transaccion/2e")
					  .then()
					  .statusCode(400)
					  .body("code", equalTo("BAD_REQUEST"))
		;
	}

	@Test
	void testGetIdNotExistThenReturnNotFound() {
		RestAssured.given()
					  .get("/transaccion/9999")
					  .then()
					  .statusCode(404)
					  .body("code", equalTo("NOT_FOUND"))
		;
	}

	@Test
	void testGetOkThenReturnTransaccionWithResiduoExpand() {
		RestAssured.given()
					  .get("/transaccion/1?expand=residuos")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaCreacion", equalTo("2023-01-01T08:00:00Z"))
					  .body("fechaRetiro", equalTo("2023-01-02T08:00:00Z"))
					  .body("transporteId", equalTo(1))
					  .body("puntoReciclajeId", equalTo(1))
					  .body("residuos", hasSize(2))
					  .body("residuos[0].id", equalTo(7))
					  .body("residuos[1].id", equalTo(8))
		;
	}

	@Test
	void testGetOkThenReturnTransaccionWithTransporteExpand() {
		RestAssured.given()
					  .get("/transaccion/1?expand=transporte")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaCreacion", equalTo("2023-01-01T08:00:00Z"))
					  .body("fechaRetiro", equalTo("2023-01-02T08:00:00Z"))
					  .body("puntoReciclajeId", equalTo(1))
					  .body("transporte.id", equalTo(1))
					  .body("transporte.fechaInicio", equalTo("2020-01-01T08:00:00Z"))
					  .body("transporte.fechaFin", equalTo("2020-01-02T08:00:00Z"))
					  .body("transporte.precioAcordado", equalTo(40.0F))
					  .body("transporte.transportistaId", equalTo(1))
					  .body("transporte.transaccionUri", equalTo("/transaccion/1"))
					  .body("transporte.transaccionId", equalTo(1))
					  .body("transporte.pagoConfirmado", equalTo(false))
					  .body("transporte.entregaConfirmada", equalTo(false))
		;
	}

	@Test
	void testGetOkThenReturnTransaccionWithPuntoReciclajeExpand() {
		RestAssured.given()
					  .get("/transaccion/1?expand=punto_reciclaje")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaCreacion", equalTo("2023-01-01T08:00:00Z"))
					  .body("fechaRetiro", equalTo("2023-01-02T08:00:00Z"))
					  .body("puntoReciclajeUri", equalTo("/reciclador/1/punto_reciclaje/1"))
					  .body("puntoReciclaje.id", equalTo(1))
					  .body("puntoReciclaje.titulo", equalTo("Prueba 1"))
					  .body("puntoReciclaje.latitud", equalTo(-34.65199F))
					  .body("puntoReciclaje.longitud", equalTo(-58.58509F))
					  .body("puntoReciclaje.recicladorId", equalTo(1))
		;
	}

}
