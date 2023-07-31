package org.circle8.integration;

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
public class ZonaGetTest {
	
	@Test
	void testGetOk() {
		RestAssured.given()
			.get("/organizacion/1/zona/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("nombre", equalTo("Zona 1"))
			.body("polyline",is(not(hasSize(0))))
			.body("organizacionUri", equalTo("/organizacion/1"))
			.body("organizacionId", equalTo(1))
			.body("organizacion", notNullValue())
			.body("tipoResiduo",hasSize(2))
			.body("tipoResiduo[0].id", equalTo(1))
			.body("tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("tipoResiduo[1].id", equalTo(2))
			.body("tipoResiduo[1].nombre", equalTo("Papel"))
		;
	}
	
	@Test
	void testGetOkWithExpandOrganizacion() {
		RestAssured.given()
			.get("/organizacion/1/zona/1?expand=organizacion")
			.then()
			.statusCode(200)
			.body("organizacion", notNullValue())
			.body("organizacion.id", equalTo(1))
			.body("organizacion.razonSocial", equalTo("Usuario 1 SA"))
			.body("organizacion.usuarioUri", equalTo("/user/1"))
			.body("organizacion.usuarioId", equalTo(1))
		;
	}
	
	@Test
	void testGetOkWithExpandRecorridos() {
		RestAssured.given()
			.get("/organizacion/1/zona/1?expand=recorridos")
			.then()
			.statusCode(200)
			.body("recorridos", hasSize(2))
			.body("recorridos[0].id", equalTo(1))
			.body("recorridos[0].fechaRetiro", equalTo("2023-07-03T03:00:00Z"))
			.body("recorridos[0].fechaInicio", equalTo("2023-07-03T10:00:00Z"))
			.body("recorridos[0].fechaFin", equalTo("2023-07-03T11:00:00Z"))
			.body("recorridos[0].recicladorId", equalTo(1))
			.body("recorridos[0].recicladorUri", equalTo("/user/3"))
			.body("recorridos[1].id", equalTo(2))
			.body("recorridos[1].fechaRetiro", equalTo("2023-07-05T03:00:00Z"))
			.body("recorridos[1].fechaInicio", nullValue())
			.body("recorridos[1].fechaFin", nullValue())
			.body("recorridos[1].recicladorId", equalTo(1))
			.body("recorridos[1].recicladorUri", equalTo("/user/3"))
		;
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
		.get("/organizacion/0/zona/0")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutOrganizacionId() {
		RestAssured.given()
		.get("/organizacion//zona/1")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutZonaId() {
		RestAssured.given()
		.get("/organizacion/1/zona/")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testInvalidOrganizacionId() {
		RestAssured.given()
		.get("/organizacion/aa/zona/1")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testInvalidZonaId() {
		RestAssured.given()
		.get("/organizacion/1/zona/aa")
		.then()
		.statusCode(400)
		;
	}

}
