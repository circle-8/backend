package org.circle8.integration.zona;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
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
public class ZonaListTest {

	@Test
	void testListOk() {
		RestAssured.given()
			.get("/zonas")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data[0].id", equalTo(1))
			.body("data[0].nombre", equalTo("Zona 1"))
			.body("data[0].polyline",is(not(hasSize(0))))
			.body("data[0].organizacionUri", equalTo("/organizacion/1"))
			.body("data[0].organizacionId", equalTo(1))
			.body("data[0].organizacion", notNullValue())
			.body("data[0].tipoResiduo",hasSize(2))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[0].tipoResiduo[1].id", equalTo(2))
			.body("data[0].tipoResiduo[1].nombre", equalTo("Papel"))
			.body("data[1].id", equalTo(2))
			.body("data[1].nombre", equalTo("Zona 2"))
			.body("data[1].polyline",is(not(hasSize(0))))
			.body("data[1].organizacionUri", equalTo("/organizacion/2"))
			.body("data[1].organizacionId", equalTo(2))
			.body("data[1].organizacion", notNullValue())
			.body("data[1].tipoResiduo",hasSize(3))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[1].tipoResiduo[1].id", equalTo(3))
			.body("data[1].tipoResiduo[1].nombre", equalTo("Pilas"))
			.body("data[1].tipoResiduo[2].id", equalTo(4))
			.body("data[1].tipoResiduo[2].nombre", equalTo("Carton"))
		;
	}

	@Test
	void testWithExpandOrganizacion() {
		RestAssured.given()
			.get("/zonas?expand=organizacion")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.organizacion.id", everyItem(notNullValue()))
			.body("data.organizacion.razonSocial", everyItem(notNullValue()))
			.body("data.organizacion.usuarioId", everyItem(notNullValue()))
		;
	}

	@Test
	void testGetOkWithExpandRecorridos() {
		RestAssured.given()
			.get("/zonas?expand=recorridos")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.recorridos", hasSize(greaterThan(0)))
		;
	}

	@Test
	void testGetOkWithExpandPuntoResiduo() {
		RestAssured.given()
			.get("/zonas?expand=punto_residuo")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.puntosResiduos", hasSize(greaterThan(0)))
			.body("data.puntosResiduos.id", everyItem(notNullValue()))
		;
	}

	@Test
	void testWithFilterTiposResiduos() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=1&tipos_residuo=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.tipoResiduo.id", everyItem(anyOf(hasItem(equalTo(1)), hasItem(equalTo(2)))))
		;
	}

	@Test
	void testWithFilterTiposResiduosNotFound() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void testWithInvalidFilterTiposResiduos() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=a")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithFilterPuntoReciduoId() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
		;

		RestAssured.given()
			.get("/zonas?punto_residuo_id=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}

	@Test
	void testWithFilterPuntoReciduoIdNotFound() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void testWithInvalidPuntoReciduoId() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=a")
			.then()
			.statusCode(400)
		;
	}


	@Test
	void testWithFilterRecicladorId() {
		RestAssured.given()
			.get("/zonas?reciclador_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}

	@Test
	void testWithFilterRecicladorIdNotFound() {
		RestAssured.given()
			.get("/zonas?reciclador_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void testWithInvalidRecicladorId() {
		RestAssured.given()
			.get("/zonas?reciclador_id=a")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithFilterCiudadanoId() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
		;

		RestAssured.given()
			.get("/zonas?ciudadano_id=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}

	@Test
	void testWithFilterCiudadanoIdNotFound() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))
		;
	}

	@Test
	void testWithInvalidCiudadanoId() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=a")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithFilterOrganizacionId() {
		RestAssured.given()
			.get("/zonas?organizacion_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.organizacion.id", everyItem(equalTo(1)))
		;
	}

	@Test
	void testWithFilterOrganizacionIdAndExpand() {
		RestAssured.given()
			.get("/zonas?organizacion_id=1&expand=organizacion")
			.then()
			.statusCode(200)
			.body("data", hasSize(greaterThan(1)))
			.body("data.organizacion.id", everyItem(equalTo(1)))
			.body("data.organizacion.razonSocial", everyItem(equalTo("Usuario 1 SA")))
		;
	}

	@Test
	void testWithFilterOrganizacionNotFound() {
		RestAssured.given()
		.get("/zonas?organizacion_id=0")
		.then()
		.statusCode(200)
		.body("data", hasSize(0))
		;
	}

	@Test
	void testWithInvalidFilterOrganizacion() {
		RestAssured.given()
		.get("/zonas?organizacion_id=a")
		.then()
		.statusCode(400)
		;
	}

}
