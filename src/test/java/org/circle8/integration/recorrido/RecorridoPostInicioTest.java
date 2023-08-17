package org.circle8.integration.recorrido;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPostInicioTest {


	private static final String REQUEST =  """
			{
				"latitud": "-34.6675104",
				"longitud": "-58.5721607"
			}""";

	@Test
	void testPostOk() {
		RestAssured.given()
					  .body(REQUEST)
					  .post("/recorrido/1/inicio")
					  .then()
					  .statusCode(200)
					  .body("puntoInicio.latitud", equalTo(-34.6675123F))
					  .body("puntoInicio.longitud", equalTo(-58.5721607F))
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
					  .post("/recorrido/1/inicio")
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
					  .post("/recorrido/1/inicio")
					  .then()
					  .statusCode(400)
		;
	}

	@Test
	void testUnexistingRecorrido() {
		RestAssured.given()
					  .body(REQUEST)
					  .post("/recorrido/10/inicio")
					  .then()
					  .statusCode(404)
		;
	}

}
