package org.circle8.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class ResiduoPostTest {
	
	@Test
	void testInsertOkWithoutFechalimite() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&ciudadano_id=1&descripcion=prueba")
			.then()
			.statusCode(200)
			.body("fechaLimiteRetiro", nullValue())
			.body("puntoResiduoUri", equalTo("/ciudadano/1/punto_residuo/1"))
			.body("puntoResiduo", not(nullValue()))
			.body("puntoResiduo.id", equalTo(1))
			.body("puntoResiduo.ciudadanoId", equalTo(1))
			.body("puntoResiduo.ciudadanoUri", equalTo("/user/1"))
			.body("puntoResiduo.ciudadano", not(nullValue()))
			.body("puntoResiduo.ciudadano.id",  equalTo(1))
			.body("tipoResiduo", not(nullValue()))
			.body("tipoResiduo.id", equalTo(1))
		;
	}
	
	@Test
	void testInsertOkWithFechalimite() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&ciudadano_id=1&descripcion=prueba&fecha_limite_retiro=2024-07-25T14:14:14.445Z")
			.then()
			.statusCode(200)
			.body("fechaLimiteRetiro", not(nullValue()))
			.body("fechaLimiteRetiro", equalTo("2024-07-25T14:14:14.445Z"))
			.body("puntoResiduoUri", equalTo("/ciudadano/1/punto_residuo/1"))
			.body("puntoResiduo", not(nullValue()))
			.body("puntoResiduo.id", equalTo(1))
			.body("puntoResiduo.ciudadanoId", equalTo(1))
			.body("puntoResiduo.ciudadanoUri", equalTo("/user/1"))
			.body("puntoResiduo.ciudadano", not(nullValue()))
			.body("puntoResiduo.ciudadano.id",  equalTo(1))
			.body("tipoResiduo", not(nullValue()))
			.body("tipoResiduo.id", equalTo(1))
		;
	}

	@Test
	void testTipoResiduoIdNotSend() {
		RestAssured.given()
			.post("/residuo?punto_residuo_id=1&descripcion=prueba&ciudadano_id=1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testPuntoResiduoIdNotSend() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&descripcion=prueba&ciudadano_id=1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testciudadanoIdNotSend() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&descripcion=prueba")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testDescipcionNotSend() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&ciudadano_id=1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testInvalidFechaLimiteFormat() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&ciudadano_id=1&descripcion=prueba&fecha_limite_retiro=2023-07-25")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testFechaLimiteBeforeToday() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=1&ciudadano_id=1&descripcion=prueba&fecha_limite_retiro=2022-07-25T14:14:14.445Z")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testInvalidTipoResiduoID() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=-1&punto_residuo_id=1&ciudadano_id=1&descripcion=prueba&fecha_limite_retiro=2024-07-25T14:14:14.445Z")
			.then()
			.statusCode(500)
		;
	}
	
	@Test
	void testInvalidPuntoResiduoID() {
		RestAssured.given()
			.post("/residuo?tipo_residuo_id=1&punto_residuo_id=-1&ciudadano_id=1&descripcion=prueba&fecha_limite_retiro=2024-07-25T14:14:14.445Z")
			.then()
			.statusCode(500)
		;
	}
}
