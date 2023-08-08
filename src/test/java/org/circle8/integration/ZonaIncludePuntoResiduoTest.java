package org.circle8.integration;

import static org.hamcrest.Matchers.hasSize;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class ZonaIncludePuntoResiduoTest {

	@Test
	void testIncludeOk() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/2/zona/2")
			.then()
			.statusCode(200)
			.body("puntosResiduos", hasSize(2))			
		;
	}
	
	@Test
	void testPuntoNotIncludeInZona() {
		RestAssured.given()
			.post("/ciudadano/3/punto_residuo/3/zona/2")
			.then()
			.statusCode(400)			
		;
	}
	
	@Test
	void testNotFoundZona() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/2/zona/0")
			.then()
			.statusCode(404)			
		;
	}
	
	@Test
	void testNotFoundPunto() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/0/zona/2")
			.then()
			.statusCode(404)			
		;
		
		RestAssured.given()
			.post("/ciudadano/0/punto_residuo/2/zona/2")
			.then()
			.statusCode(404)			
		;
	}
	
	@Test
	void testWithOutPunto() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo//zona/2")
			.then()
			.statusCode(404)			
		;
		
		RestAssured.given()
			.post("/ciudadano//punto_residuo/2/zona/2")
			.then()
			.statusCode(404)			
		;
	}
	
	@Test
	void testInvalidCiudadanoId() {
		RestAssured.given()
			.post("/ciudadano/a/punto_residuo/2/zona/2")
			.then()
			.statusCode(400)			
		;
	}
	
	@Test
	void testInvalidPuntoId() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/a/zona/2")
			.then()
			.statusCode(400)			
		;
	}
	
	@Test
	void testWithOutZona() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/2/zona/")
			.then()
			.statusCode(404)			
		;
	}
	
	@Test
	void testInvalidZonaId() {
		RestAssured.given()
			.post("/ciudadano/2/punto_residuo/2/zona/a")
			.then()
			.statusCode(400)			
		;
	}
}
