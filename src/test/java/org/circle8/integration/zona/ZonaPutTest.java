package org.circle8.integration.zona;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class ZonaPutTest {
	
	@Test
	void testPutOk() {
		String body =  """
				{
				"nombre": "Zona put 1",
				"polyline": [
				{"latitud":-34.6430956,"longitud":-58.5951324},
			    {"latitud":-34.6432267,"longitud":-58.5948434},
			    {"latitud":-34.6428927,"longitud":-58.5953719},
			    {"latitud":-34.6429311,"longitud":-58.5953171}],
			    "tiposResiduo": [1]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("nombre", equalTo("Zona put 1"))
			.body("polyline",is(not(hasSize(0))))
			.body("organizacionUri", equalTo("/organizacion/1"))
			.body("organizacionId", equalTo(1))
			.body("organizacion", notNullValue())
			.body("tipoResiduo",hasSize(1))
			.body("tipoResiduo[0].id", equalTo(1))
			.body("tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("puntosResiduos", is(nullValue()))
		;
	}
	
	@Test
	void testInvalidTipoId() {
		String body =  """
				{
				"nombre": "Zona put 1",
				"polyline": [
				{"latitud":-34.6430956,"longitud":-58.5951324},
			    {"latitud":-34.6432267,"longitud":-58.5948434},
			    {"latitud":-34.6428927,"longitud":-58.5953719},
			    {"latitud":-34.6429311,"longitud":-58.5953171}],
			    "tiposResiduo": [0]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(404)
		;
	}
	
	@Test
	void testWithOutName() {
		String body =  """
				{
				"polyline": [
				{"latitud":-34.6430956,"longitud":-58.5951324},
			    {"latitud":-34.6432267,"longitud":-58.5948434},
			    {"latitud":-34.6428927,"longitud":-58.5953719},
			    {"latitud":-34.6429311,"longitud":-58.5953171}],
			    "tiposResiduo": [1,2,3]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testInvalidPolyline() {
		String body =  """
				{
				"nombre": "Zona put 1",
				"polyline": [
				{"latitud":-34.6430956,"longitud":-58.5951324},
			    {"latitud":-34.6432267,"longitud":-58.5948434}],
			    "tiposResiduo": [1,2,3]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testWithOutPolyline() {
		String body =  """
				{
				"nombre": "Zona put 1",				
			    "tiposResiduo": [1,2,3]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testWithOutTipos() {
		String body =  """
				{
				"nombre": "Zona put 1",
				"polyline": [
				{"latitud":-34.6430956,"longitud":-58.5951324},
			    {"latitud":-34.6432267,"longitud":-58.5948434},
			    {"latitud":-34.6428927,"longitud":-58.5953719},
			    {"latitud":-34.6429311,"longitud":-58.5953171}]
			    }""";
		
		RestAssured.given()
			.body(body)
			.put("/organizacion/1/zona/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutOrganizacionId() {
		RestAssured.given()
		.put("/organizacion//zona/1")
		.then()
		.statusCode(404)
		;
	}
	
	
	@Test
	void testInvalidOrganizacionId() {
		RestAssured.given()
		.put("/organizacion/aa/zona/1")
		.then()
		.statusCode(400)
		;
	}
	
	@Test
	void testWithOutZonaId() {
		RestAssured.given()
		.put("/organizacion/1/zona/")
		.then()
		.statusCode(404)
		;
	}
	
	
	@Test
	void testInvalidZonaId() {
		RestAssured.given()
		.put("/organizacion/1/zona/aa")
		.then()
		.statusCode(400)
		;
	}
}
