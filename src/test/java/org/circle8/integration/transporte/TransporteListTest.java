package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
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
public class TransporteListTest {

	@Test
	void ListWithoutFilterExpands() {
		RestAssured.given()
			.get("/transportes")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
			.body("data[0].precioAcordado", equalTo(40.0F))
			.body("data[0].fechaAcordada", is(nullValue()))
			.body("data[0].fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("data[0].fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("data[0].pagoConfirmado", equalTo(false))
			.body("data[0].entregaConfirmada", equalTo(false))
			.body("data[0].precioSugerido", equalTo(2500.0F))
			
			.body("data[1].id", equalTo(2))
			.body("data[1].precioAcordado", equalTo(50.0F))
			.body("data[1].fechaAcordada", is(nullValue()))
			.body("data[1].fechaInicio", equalTo("2020-02-01T08:00:00Z"))
			.body("data[1].fechaFin", equalTo("2020-02-02T08:00:00Z"))
			.body("data[1].pagoConfirmado", equalTo(true))
			.body("data[1].entregaConfirmada", equalTo(true))
			.body("data[1].precioSugerido", equalTo(0))
			
			.body("data[2].id", equalTo(3))
			.body("data[2].precioAcordado", equalTo(60.0F))
			.body("data[2].fechaAcordada", equalTo("2020-03-02"))
			.body("data[2].fechaInicio", is(nullValue()))
			.body("data[2].fechaFin", is(nullValue()))
			.body("data[2].pagoConfirmado", equalTo(true))
			.body("data[2].entregaConfirmada", equalTo(false))
			.body("data[2].precioSugerido", equalTo(1500.0F))
		;
	}
	
	@Test
	void ListWithoutFilterWithExpandTransportista() {
		RestAssured.given()
			.get("/transportes?expand=transportista")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
					.body("data[0].transportistaUri", equalTo("/user/1"))
			.body("data[0].transportistaId", equalTo(1))
			.body("data[0].transportista", notNullValue())
			.body("data[0].transportista.id", equalTo(1))
			.body("data[0].transportista.polylineAlcance", is(not(hasSize(0))))
			
			.body("data[1].id", equalTo(2))
			.body("data[1].transportistaUri", equalTo("/user/2"))
			.body("data[1].transportistaId", equalTo(2))
			.body("data[1].transportista", notNullValue())
			.body("data[1].transportista.id", equalTo(2))
			.body("data[1].transportista.polylineAlcance", is(not(hasSize(0))))
			
			.body("data[2].id", equalTo(3))
			.body("data[2].transportistaId", is(nullValue()))
			.body("data[2].transportista", is(nullValue()))
		;
	}
	
	@Test
	void ListWithoutFilterWithExpandTransaccion() {
		RestAssured.given()
			.get("/transportes?expand=transaccion")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
			.body("data[0].transaccionId", equalTo(1))			
			.body("data[0].transaccionUri", equalTo("/transaccion/1"))
			
			.body("data[1].id", equalTo(2))
			.body("data[1].transaccionId", equalTo(2))			
			.body("data[1].transaccionUri", equalTo("/transaccion/2"))
			
			.body("data[2].id", equalTo(2))
			.body("data[2].transaccionId", equalTo(3))			
			.body("data[2].transaccionUri", equalTo("/transaccion/3"))
			
			.body("data[3].id", equalTo(3))
			.body("data[3].transaccionId", is(nullValue()))			
			.body("data[3].transaccionUri", is(nullValue()))
		;
	}
	
	@Test
	void ListWithoutFilterWithExpands() {
		RestAssured.given()
			.get("/transportes?expand=transaccion&expand=transportista")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
			.body("data[0].precioAcordado", equalTo(40.0F))
			.body("data[0].fechaAcordada", is(nullValue()))
			.body("data[0].fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("data[0].fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("data[0].pagoConfirmado", equalTo(false))
			.body("data[0].entregaConfirmada", equalTo(false))
			.body("data[0].precioSugerido", equalTo(2500.0F))
			.body("data[0].transaccionId", equalTo(1))			
			.body("data[0].transaccionUri", equalTo("/transaccion/1"))
			.body("data[0].transportistaUri", equalTo("/user/1"))
			.body("data[0].transportistaId", equalTo(1))
			.body("data[0].transportista", notNullValue())
			.body("data[0].transportista.id", equalTo(1))
			.body("data[0].transportista.polylineAlcance", is(not(hasSize(0))))
			
			.body("data[1].id", equalTo(2))
			.body("data[1].precioAcordado", equalTo(50.0F))
			.body("data[1].fechaAcordada", is(nullValue()))
			.body("data[1].fechaInicio", equalTo("2020-02-01T08:00:00Z"))
			.body("data[1].fechaFin", equalTo("2020-02-02T08:00:00Z"))
			.body("data[1].pagoConfirmado", equalTo(true))
			.body("data[1].entregaConfirmada", equalTo(true))
			.body("data[1].precioSugerido", equalTo(0))
			.body("data[1].transaccionId", equalTo(2))			
			.body("data[1].transaccionUri", equalTo("/transaccion/2"))
			.body("data[1].transportistaUri", equalTo("/user/2"))
			.body("data[1].transportistaId", equalTo(2))
			.body("data[1].transportista", notNullValue())
			.body("data[1].transportista.id", equalTo(2))
			.body("data[1].transportista.polylineAlcance", is(not(hasSize(0))))
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
			.body("data[0].precioAcordado", equalTo(40.0F))
			.body("data[0].fechaAcordada", is(nullValue()))
			.body("data[0].fechaInicio", equalTo("2020-01-01T08:00:00Z"))
			.body("data[0].fechaFin", equalTo("2020-01-02T08:00:00Z"))
			.body("data[0].pagoConfirmado", equalTo(false))
			.body("data[0].entregaConfirmada", equalTo(false))
			.body("data[0].precioSugerido", equalTo(2500.0F))			
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
			.body("data[0].id", equalTo(1))
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
			.body("data[0].id", equalTo(2))		
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
			.body("data[0].id", equalTo(3))		
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
			.body("data[0].id", equalTo(1))
			.body("data[0].transportistaId", equalTo(1))	
			.body("data[1].id", equalTo(2))
			.body("data[1].transportistaId", equalTo(2))	
			.body("data[2].id", equalTo(3))
			.body("data[2].transportistaId", is(nullValue()))	
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
