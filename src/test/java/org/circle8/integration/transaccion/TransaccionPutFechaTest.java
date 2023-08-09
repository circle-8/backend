package org.circle8.integration.transaccion;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class TransaccionPutFechaTest {

	@Test
	void testPutOk() {
		var request = """
			{
			    "fechaRetiro": "2023-09-10T15:30:00+03:00"
			}
			""";
		RestAssured.given()
					  .body(request)
					  .put("/transaccion/2")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(2))
					  .body("fechaRetiro", equalTo("2023-09-10T12:30:00Z"));
	}

	@Test
	void testTransaccionNotExist() {
		var request = """
			{
			    "fechaRetiro": "2023-09-10T15:30:00+03:00"
			}
			""";
		RestAssured.given()
					  .body(request)
					  .put("/transaccion/10")
					  .then()
					  .statusCode(500);
	}

	@Test
	void testWrongDateName() {
		var request = """
			{
			    "fechaRtiro": "2023-09-10T15:30:00+03:00"
			}
			""";
		RestAssured.given()
					  .body(request)
					  .put("/transaccion/2")
					  .then()
					  .statusCode(400);
	}

	@Test
	void testWrongDateInfo() {
		var request = """
			{
			    "fechaRetiro": "2023a-09-10T15:30:00+03:00"
			}
			""";
		RestAssured.given()
					  .body(request)
					  .put("/transaccion/2")
					  .then()
					  .statusCode(500);
	}

}
