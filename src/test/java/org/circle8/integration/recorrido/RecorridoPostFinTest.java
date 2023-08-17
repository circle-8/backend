package org.circle8.integration.recorrido;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPostFinTest {

	private static final String REQUEST =  """
			{
				"latitud": "-34.6675104",
				"longitud": "-58.5721607"
			}""";

	@Test
	void testPostOk() {
		RestAssured.given()
					  .body(REQUEST)
					  .post("/recorrido/1/fin")
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
					  .post("/recorrido/1/fin")
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
					  .post("/recorrido/1/fin")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testUnexistingRecorrido() {
		RestAssured.given()
					  .body(REQUEST)
					  .post("/recorrido/10/fin")
					  .then()
					  .statusCode(404)
		;
	}
}
