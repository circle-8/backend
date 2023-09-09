package org.circle8.integration.transporte;

import static org.hamcrest.Matchers.equalTo;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransportePutTest {

	@Test
	void testPutAllOk() {
		var request = """
				{
				    "precioAcordado": 500,
				    "fechaAcordada": 2023-09-09,
				    "transportistaId": 1
				}
				""";
		RestAssured.given()
			.body(request)
			.put("/transporte/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("precioAcordado", equalTo(500.0F))
			.body("fechaAcordada", equalTo("2023-09-09"))
			.body("transportistaId", equalTo(1))
		;
	}	
	
	@Test
	void testPutPrecioOk() {
		var request = """
				{
				    "precioAcordado": 500
				}
				""";
		RestAssured.given()
			.body(request)
			.put("/transporte/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("precioAcordado", equalTo(500.0F))		
		;
	}	
	
	@Test
	void testPutFechaAcordadaOk() {
		var request = """
				{
				    "fechaAcordada": 2023-09-09
				}
				""";
		RestAssured.given()
			.body(request)
			.put("/transporte/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("fechaAcordada", equalTo("2023-09-09"))	
		;
	}	
	
	@Test
	void testPutTransportistaOk() {
		var request = """
				{
				    "transportistaId": 1
				}
				""";
		RestAssured.given()
			.body(request)
			.put("/transporte/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("transportistaId", equalTo(1))		
		;
	}	

	@Test
	void testNotFound() {
		var request = """
				{
				    "precioAcordado": 500
				}
				""";
		RestAssured.given()
		.body(request)
		.put("/transporte/0")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutId() {
		var request = """
				{
				    "precioAcordado": 500
				}
				""";
		RestAssured.given()
		.body(request)
		.put("/transporte/")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithEmptyBody() {		
		RestAssured.given()
		.body("{}")
		.put("/transporte/1")
		.then()
		.statusCode(400)
		;
	}

	@Test
	void testInvalidId() {
		var request = """
				{
				    "precioAcordado": 500
				}
				""";
		RestAssured.given()
		.body(request)
		.put("/transporte/aa")
		.then()
		.statusCode(400)
		;
	}

}
