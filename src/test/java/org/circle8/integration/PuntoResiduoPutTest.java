package org.circle8.integration;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class PuntoResiduoPutTest {
	
	private static final String REQUEST =  """
			{
				"latitud": "-34.6675104",
				"longitud": "-58.5721607"
			}""";

	@Test
	void testPutOk() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/1/punto_residuo/1")
		.then()
		.statusCode(200)
		;
	}
	
	@Test
	void testWithOutLatitud() {	
		String request = """
			{
				"longitud": "-58.5721607"
			}""";
		RestAssured.given()
		.body(request)
		.put("/ciudadano/1/punto_residuo/1")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutLongitud() {
		String request = """
			{
				"latitud": "-34.6675104"
			}""";
		RestAssured.given()
		.body(request)
		.put("/ciudadano/1/punto_residuo/1")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutParams() {		
		RestAssured.given()
		.put("/ciudadano/1/punto_residuo/1")
		.then()
		.statusCode(500)
		;
	}
	
	@Test
	void testNotFoundCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/0/punto_residuo/1")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testNotFoundPuntoResiduoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/1/punto_residuo/0")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testWithOutPuntoResiduoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/1/punto_residuo/")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testInvalidPuntoResiduoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/1/punto_residuo/dfff")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano//punto_residuo/1")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void tesInvalidCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.put("/ciudadano/hhh/punto_residuo/1")
		.then()
		.statusCode(400)
		;
	}
}
