package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PuntoReciclajePostTest {

	private static final String BODY =  """
		{
		 	"latitud": -99.651840,
		    "longitud": -99.580990,
			"titulo": "MadePoints",
		   	"dias": [1,2],
			"tiposResiduo": [1]
		}""";

//	@Test
//	void testPutOk() {
//		RestAssured.given()
//			.body(BODY)
//			.post("/reciclador/1/punto_reciclaje")
//			.then()
//			.statusCode(200)
//		;
//	}

	@Test
	void testPutNotFoundByTipoResiduo() {
		String bodyNotFoundTipo =  """
   		{
   			"latitud": -35.651840,
		    "longitud": -57.580990,
			"titulo": "MadePoints",
		   	"dias": [ 1,2,3],
			"tiposResiduo": [2,400]
		}""";

		RestAssured.given()
			.body(bodyNotFoundTipo)
			.post("/reciclador/1/punto_reciclaje")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testWithOutParams() {
		RestAssured.given()
			.post("/reciclador/1/punto_reciclaje")
			.then()
			.statusCode(500)
		;
	}

	@Test
	void testInvalidRecicladorID() {
		RestAssured.given()
			.body(BODY)
			.post("/reciclador/asd/punto_reciclaje")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWhitoutRecicladorID() {
		RestAssured.given()
			.body(BODY)
			.post("/reciclador//punto_reciclaje")
			.then()
			.statusCode(404)
		;
	}
}
