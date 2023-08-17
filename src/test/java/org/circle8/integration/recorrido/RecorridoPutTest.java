package org.circle8.integration.recorrido;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ApiTestExtension.class)
public class RecorridoPutTest {

	@Test
	void testOkWithOnlyReciclador() {
		String request = """
			{
				"recicladorId": "2"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("recicladorId", equalTo(2))
		;
	}

	@Test
	void testOkWithOnlyFechaRetiro() {
		String request = """
			{
				"fechaRetiro": "2023-08-11"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaRetiro", equalTo("2023-08-11"))
		;
	}

	@Test
	void testOkWithFechaRetiroAndReciclador() {
		String request = """
			{
				"fechaRetiro": "2023-08-12",
				"recicladorId": "2"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/1")
					  .then()
					  .statusCode(200)
					  .body("id", equalTo(1))
					  .body("fechaRetiro", equalTo("2023-08-12"))
					  .body("recicladorId", equalTo(2))
		;
	}

	@Test
	void testWithWrongZona() {
		String request = """
			{
				"fechaRetiro": "2023-08-12",
				"recicladorId": "2"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/2/recorrido/1")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWithInexistingRecorrido() {
		String request = """
			{
				"fechaRetiro": "2023-08-12",
				"recicladorId": "2"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/10")
					  .then()
					  .statusCode(404)
		;
	}

	@Test
	void testWithInexistingRecicladorId() {
		String request = """
			{
				"fechaRetiro": "2023-08-12",
				"recicladorId": "20"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/1")
					  .then()
					  .statusCode(500)
		;
	}

	@Test
	void testWithWrongRecicladorId() {
		String request = """
			{
				"fechaRetiro": "2023-08-12",
				"recicladorId": "20a"
			}""";
		RestAssured.given()
					  .body(request)
					  .put("/organizacion/1/zona/1/recorrido/1")
					  .then()
					  .statusCode(400)
		;
	}
}
