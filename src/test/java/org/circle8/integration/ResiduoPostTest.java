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
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba"
			   }""";
		
		RestAssured.given()
			.body(request)
			.post("/residuo")
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
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba",
			     "fecha_limite_retiro": '2023-07-25T14:14:14.445Z'
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(200)
			.body("fechaLimiteRetiro", not(nullValue()))
			.body("fechaLimiteRetiro", equalTo("2023-07-25T14:14:14.445Z"))
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
		var request = """
				{
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba",
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testPuntoResiduoIdNotSend() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba",
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testciudadanoIdNotSend() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "descripcion": "prueba",
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testDescipcionNotSend() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testInvalidFechaLimiteFormat() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba",
			     "fecha_limite_retiro": '2023-07-25'
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(500)
		;
	}
	
	@Test
	void testFechaLimiteBeforeToday() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba",
			     "fecha_limite_retiro": '2022-07-25T14:14:14.445Z'
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testInvalidTipoResiduoID() {
		var request = """
				{
			     "tipo_residuo_id": 0,
			     "punto_residuo_id": 1,
			     "ciudadano_id": 1,
			     "descripcion": "prueba"
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(500)
		;
	}
	
	@Test
	void testInvalidPuntoResiduoID() {
		var request = """
				{
			     "tipo_residuo_id": 1,
			     "punto_residuo_id": 0,
			     "ciudadano_id": 1,
			     "descripcion": "prueba"
			   }""";
		RestAssured.given()
			.body(request)
			.post("/residuo")
			.then()
			.statusCode(500)
		;
	}
}
