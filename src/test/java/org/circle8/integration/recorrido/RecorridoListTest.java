package org.circle8.integration.recorrido;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@ExtendWith(ApiTestExtension.class)
class RecorridoListTest {
	@Test void testListRecicladorFilter() {
		RestAssured.given()
			.get("/recorridos?reciclador_id=1")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.recicladorId", everyItem(equalTo(1)))
		;
	}

	@Test void testListOrganizacionFilter() {
		RestAssured.given()
			.get("/recorridos?organizacion_id=1")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.zona.organizacionId", everyItem(equalTo(1)))
		;
	}

	@Test void testListZonaFilter() {
		RestAssured.given()
			.get("/recorridos?zona_id=1")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.zona.id", everyItem(equalTo(1)))
			.body("data.zonaId", everyItem(equalTo(1)))
		;
	}

	@Test void testListFechaRetiroFilter() {
		RestAssured.given()
			.get("/recorridos?fecha_retiro=2023-07-03")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.fechaRetiro", everyItem(equalTo("2023-07-03")))
		;
	}
	
	@Test void testListExpandZona() {
		RestAssured.given()
			.get("/recorridos?expand=zona")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.zona", everyItem(notNullValue()))
			.body("data[0].zona", notNullValue())
			.body("data[0].zona.id", equalTo(1))
			.body("data[0].zona.nombre", equalTo("Zona 1"))
		;
	}
	
	@Test void testListExpandReciclador() {
		RestAssured.given()
			.get("/recorridos?expand=reciclador")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.reciclador", everyItem(notNullValue()))
			.body("data[0].reciclador", notNullValue())
			.body("data[0].reciclador.id", equalTo(1))
			.body("data[0].reciclador.username", equalTo("reciclador1"))
		;
	}
	
	@Test void testListExpandResiduos() {
		RestAssured.given()
			.get("/recorridos?expand=residuos")
			.then()
			.body("data", hasSize(greaterThan(1)))
			.body("data.puntos", everyItem(notNullValue()))
		;
	}
}
