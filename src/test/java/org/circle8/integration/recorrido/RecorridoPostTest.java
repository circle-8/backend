package org.circle8.integration.recorrido;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;

@ExtendWith(ApiTestExtension.class)
 class RecorridoPostTest {
	@Test
	void testOK() {
		RestAssured.given()
			.body("""
			{
			  "fechaRetiro": "2023-07-03",
			  "recicladorId": 1,
			  "puntoInicio": {
			    "latitud": -34.634743,
			    "longitud": -58.558754
			  },
			  "puntoFin": {
			    "latitud": -34.650543,
			    "longitud": -58.568355
			  }
			}
			""")
			.post("/organizacion/1/zona/1/recorrido")
			.then()
			.statusCode(200)
			.body("fechaRetiro", equalTo("2023-07-03"))
		;
	}

	@Test
	void testRecicladorNotFound() {
		RestAssured.given()
			.body("""
			{
			  "fechaRetiro": "2023-07-03",
			  "recicladorId": 100,
			  "puntoInicio": {
			    "latitud": -34.634743,
			    "longitud": -58.558754
			  },
			  "puntoFin": {
			    "latitud": -34.650543,
			    "longitud": -58.568355
			  }
			}
			""")
			.post("/organizacion/1/zona/1/recorrido")
			.then()
			.statusCode(400)
			.body("message", equalTo("No existe el reciclador"))
		;
	}

	@Test
	void testInvalidBody() {
		RestAssured.given()
			.body("""
			{
			  "fechaRetiro": "2023-07-03",
			}
			""")
			.post("/organizacion/1/zona/1/recorrido")
			.then()
			.statusCode(400)
		;
	}
}
