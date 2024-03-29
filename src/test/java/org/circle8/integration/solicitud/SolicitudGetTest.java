package org.circle8.integration.solicitud;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@ExtendWith(ApiTestExtension.class)
class SolicitudGetTest {

	@Test
	void testGetOK() {
		RestAssured.given()
		.get("/solicitud/1")
		.then()
		.statusCode(200)
		.body("id", equalTo(1))
		.body("solicitanteId", equalTo(2))
		.body("solicitanteUri", equalTo("/user/2"))
		.body("solicitante", not(nullValue()))
		.body("solicitadoId", not(nullValue()))
		.body("solicitadoUri", equalTo("/user/1"))
		.body("solicitado", not(nullValue()))
		.body("estado", equalTo("PENDIENTE"))
		;
	}

	@Test
	void testGetOKExpandCiudadanos() {
		RestAssured.given()
			.get("/solicitud/1?expand=ciudadanos")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("solicitante.id", equalTo(2))
			.body("solicitante.nombre", not(nullValue()))
			.body("solicitado.id", equalTo(1))
			.body("solicitado.nombre", not(nullValue()))
		;
	}

	@Test
	void testGetOKExpandPuntoReciclaje() {
		RestAssured.given()
			.get("/solicitud/1?expand=punto_reciclaje")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("puntoReciclaje.id", equalTo(5))
			.body("puntoReciclaje.titulo", equalTo("Punto de Usuario 2"))
		;
	}

	@Test
	void testGetOKExpandResiduo() {
		RestAssured.given()
			.get("/solicitud/1?expand=residuo")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("residuo.id", equalTo(1))
			.body("residuo.descripcion", equalTo("Prueba 1"))
		;
	}

	@Test
	void testGetOKExpandResiduoAndCiudadanos() {
		RestAssured.given()
			.get("/solicitud/1?expand=residuo&expand=ciudadanos")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("residuo.id", equalTo(1))
			.body("residuo.descripcion", equalTo("Prueba 1"))
			.body("solicitante.id", equalTo(2))
			.body("solicitante.nombre", not(nullValue()))
			.body("solicitado.id", equalTo(1))
			.body("solicitado.nombre", not(nullValue()))
		;
	}

	@Test
	void testGetOKEstadoExpirado() {
		RestAssured.given()
		.get("/solicitud/2")
		.then()
		.statusCode(200)
		.body("estado", equalTo("EXPIRADA"))
		;
	}

	@Test
	void testNotFound() {
		RestAssured.given()
		.get("/solicitud/0")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutSolicitudId() {
		RestAssured.given()
		.get("/solicitud/")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testInvalidSolicitudId() {
		RestAssured.given()
		.get("/solicitud/das")
		.then()
		.statusCode(400)
		;
	}

}
