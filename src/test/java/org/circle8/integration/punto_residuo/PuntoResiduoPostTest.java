package org.circle8.integration.punto_residuo;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class PuntoResiduoPostTest {
	
	private static final String REQUEST =  """
			{
				"latitud": "-34.6675104",
				"longitud": "-58.5721607"
			}""";

	@Test
	void testPostOk() {		
		RestAssured.given()
		.body(REQUEST)
		.post("/ciudadano/1/punto_residuo")
		.then()
		.statusCode(200)
		.body("id", is(not(emptyOrNullString())))
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
		.post("/ciudadano/1/punto_residuo")
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
		.post("/ciudadano/1/punto_residuo")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutParams() {		
		RestAssured.given()
		.post("/ciudadano/1/punto_residuo")
		.then()
		.statusCode(500)
		;
	}
	
	@Test
	void testNotFoundCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.post("/ciudadano/0/punto_residuo")
		.then()
		.statusCode(404)
		;
	}
	
	@Test
	void testInvalidCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.post("/ciudadano/dasda/punto_residuo")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutCiudadanoID() {		
		RestAssured.given()
		.body(REQUEST)
		.post("/ciudadano//punto_residuo")
		.then()
		.statusCode(404)
		;
	}
}
