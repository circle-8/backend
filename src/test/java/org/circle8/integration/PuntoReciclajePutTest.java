package org.circle8.integration;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class PuntoReciclajePutTest {

	private static final String BODY =  """
		{
		 	"latitud": -99.651840,
		    "longitud": -99.580990,
			"titulo": "MadePoints",
		   	"dias": [1,2],
			"tiposResiduo": [1]
		}""";

	@Test
	void testPutOk() {
		RestAssured.given()
			.body(BODY)
			.put("/reciclador/1/punto_reciclaje/1")
			.then()
			.statusCode(200)
		;
	}
	@Test
	void testPutNotFoundByRecicladorId() {
		RestAssured.given()
			.body(BODY)
			.put("/reciclador/800/punto_reciclaje/1")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testPutNotFoundByPuntoReciclaje() {
		RestAssured.given()
			.body(BODY)
			.put("/reciclador/1/punto_reciclaje/800")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testPutNotFoundByTipoResiduo() {
		String bodyNotFoundTipo =  """
   		{
			"tiposResiduo": [2,400]
		}""";

		RestAssured.given()
			.body(bodyNotFoundTipo)
			.put("/reciclador/1/punto_reciclaje/1")
			.then()
			.statusCode(404)
		;
	}

	@Test
	void testWithOutParams() {
		RestAssured.given()
			.put("/reciclador/1/punto_reciclaje/1")
			.then()
			.statusCode(500)
		;
	}
}
