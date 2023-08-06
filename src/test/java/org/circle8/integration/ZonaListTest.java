package org.circle8.integration;

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
public class ZonaListTest {
	
	@Test
	void testListOk() {
		RestAssured.given()
			.get("/zonas")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].id", equalTo(1))
			.body("data[0].nombre", equalTo("Zona 1"))
			.body("data[0].polyline",is(not(hasSize(0))))
			.body("data[0].organizacionUri", equalTo("/organizacion/1"))
			.body("data[0].organizacionId", equalTo(1))
			.body("data[0].organizacion", notNullValue())
			.body("data[0].tipoResiduo",hasSize(2))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[0].tipoResiduo[1].id", equalTo(2))
			.body("data[0].tipoResiduo[1].nombre", equalTo("Papel"))			
			.body("data[1].id", equalTo(2))
			.body("data[1].nombre", equalTo("Zona 2"))
			.body("data[1].polyline",is(not(hasSize(0))))
			.body("data[1].organizacionUri", equalTo("/organizacion/2"))
			.body("data[1].organizacionId", equalTo(2))
			.body("data[1].organizacion", notNullValue())
			.body("data[1].tipoResiduo",hasSize(3))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[1].tipoResiduo[1].id", equalTo(3))
			.body("data[1].tipoResiduo[1].nombre", equalTo("Pilas"))
			.body("data[1].tipoResiduo[2].id", equalTo(4))
			.body("data[1].tipoResiduo[2].nombre", equalTo("Carton"))
		;
	}
	
	@Test
	void testWithExpandOrganizacion() {
		RestAssured.given()
			.get("/zonas?expand=organizacion")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].organizacion", notNullValue())
			.body("data[0].organizacion.id", equalTo(1))
			.body("data[0].organizacion.razonSocial", equalTo("Usuario 1 SA"))
			.body("data[0].organizacion.usuarioUri", equalTo("/user/1"))
			.body("data[0].organizacion.usuarioId", equalTo(1))
			.body("data[1].organizacion", notNullValue())
			.body("data[1].organizacion.id", equalTo(2))
			.body("data[1].organizacion.razonSocial", equalTo("Usuario 2 SA"))
			.body("data[1].organizacion.usuarioUri", equalTo("/user/2"))
			.body("data[1].organizacion.usuarioId", equalTo(2))
		;
	}
	
	@Test
	void testGetOkWithExpandRecorridos() {
		RestAssured.given()
			.get("/zonas?expand=recorridos")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))		
			.body("data[0].recorridos", hasSize(2))
			.body("data[0].recorridos[0].id", equalTo(1))
			.body("data[0].recorridos[0].fechaRetiro", equalTo("2023-07-03"))
			.body("data[0].recorridos[0].fechaInicio", equalTo("2023-07-03T10:00:00Z"))
			.body("data[0].recorridos[0].fechaFin", equalTo("2023-07-03T11:00:00Z"))
			.body("data[0].recorridos[0].recicladorId", equalTo(1))
			.body("data[0].recorridos[0].recicladorUri", equalTo("/user/3"))
			.body("data[0].recorridos[1].id", equalTo(2))
			.body("data[0].recorridos[1].fechaRetiro", equalTo("2023-07-05"))
			.body("data[0].recorridos[1].fechaInicio", nullValue())
			.body("data[0].recorridos[1].fechaFin", nullValue())
			.body("data[0].recorridos[1].recicladorId", equalTo(1))
			.body("data[0].recorridos[1].recicladorUri", equalTo("/user/3"))
		;
	}
	
	@Test
	void testGetOkWithExpandPuntoResiduo() {
		RestAssured.given()
			.get("/zonas?expand=punto_residuo")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].puntosResiduos", notNullValue())
			.body("data[0].puntosResiduos", hasSize(2))
			.body("data[0].puntosResiduos[0].id", equalTo(1))
			.body("data[0].puntosResiduos[0].latitud", equalTo(-34.6611203f))
			.body("data[0].puntosResiduos[0].longitud", equalTo(-58.5422521f))
			.body("data[0].puntosResiduos[0].ciudadanoId", equalTo(1))
			.body("data[0].puntosResiduos[1].id", equalTo(2))
			.body("data[0].puntosResiduos[1].latitud", equalTo(-35.6611203f))
			.body("data[0].puntosResiduos[1].longitud", equalTo(-58.5422521f))
			.body("data[0].puntosResiduos[1].ciudadanoId", equalTo(2))
			.body("data[1].puntosResiduos", notNullValue())
			.body("data[1].puntosResiduos", hasSize(1))
			.body("data[1].puntosResiduos[0].id", equalTo(1))
			.body("data[1].puntosResiduos[0].latitud", equalTo(-34.6611203f))
			.body("data[1].puntosResiduos[0].longitud", equalTo(-58.5422521f))
			.body("data[1].puntosResiduos[0].ciudadanoId", equalTo(1))
		;
	}
	
	@Test
	void testWithFilterTiposResiduos() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=1&tipos_residuo=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
			.body("data[0].id", equalTo(1))			
			.body("data[0].tipoResiduo",hasSize(2))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[0].tipoResiduo[1].id", equalTo(2))
			.body("data[0].tipoResiduo[1].nombre", equalTo("Papel"))			
			.body("data[1].id", equalTo(2))			
			.body("data[1].tipoResiduo",hasSize(3))
			.body("data[0].tipoResiduo[0].id", equalTo(1))
			.body("data[0].tipoResiduo[0].nombre", equalTo("Plástico"))
			.body("data[1].tipoResiduo[1].id", equalTo(3))
			.body("data[1].tipoResiduo[1].nombre", equalTo("Pilas"))
			.body("data[1].tipoResiduo[2].id", equalTo(4))
			.body("data[1].tipoResiduo[2].nombre", equalTo("Carton"))		
		;
	}
	
	@Test
	void testWithFilterTiposResiduosNotFound() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))			
		;
	}
	
	@Test
	void testWithInvalidFilterTiposResiduos() {
		RestAssured.given()
			.get("/zonas?tipos_residuo=a")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithFilterPuntoReciduoId() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
		;
		
		RestAssured.given()
			.get("/zonas?punto_residuo_id=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}
	
	@Test
	void testWithFilterPuntoReciduoIdNotFound() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))			
		;
	}
	
	@Test
	void testWithInvalidPuntoReciduoId() {
		RestAssured.given()
			.get("/zonas?punto_residuo_id=a")
			.then()
			.statusCode(400)		
		;
	}
	
	
	@Test
	void testWithFilterRecicladorId() {
		RestAssured.given()
			.get("/zonas?reciclador_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}
	
	@Test
	void testWithFilterRecicladorIdNotFound() {
		RestAssured.given()
			.get("/zonas?reciclador_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))			
		;
	}
	
	@Test
	void testWithInvalidRecicladorId() {
		RestAssured.given()
			.get("/zonas?reciclador_id=a")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithFilterCiudadanoId() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(2))
		;
		
		RestAssured.given()
			.get("/zonas?ciudadano_id=2")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
		;
	}
	
	@Test
	void testWithFilterCiudadanoIdNotFound() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=0")
			.then()
			.statusCode(200)
			.body("data", hasSize(0))			
		;
	}
	
	@Test
	void testWithInvalidCiudadanoId() {
		RestAssured.given()
			.get("/zonas?ciudadano_id=a")
			.then()
			.statusCode(400)		
		;
	}
	
	@Test
	void testWithFilterOrganizacionId() {
		RestAssured.given()
			.get("/zonas?organizacion_id=1")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].organizacion", notNullValue())
			.body("data[0].organizacion.id", equalTo(1))
		;
	}
	
	@Test
	void testWithFilterOrganizacionIdAndExpand() {
		RestAssured.given()
			.get("/zonas?organizacion_id=1&expand=organizacion")
			.then()
			.statusCode(200)
			.body("data", hasSize(1))
			.body("data[0].organizacion", notNullValue())
			.body("data[0].organizacion.id", equalTo(1))
			.body("data[0].organizacion.razonSocial", equalTo("Usuario 1 SA"))
			.body("data[0].organizacion.usuarioUri", equalTo("/user/1"))
			.body("data[0].organizacion.usuarioId", equalTo(1))
		;
	}
	
	@Test
	void testWithFilterOrganizacionNotFound() {
		RestAssured.given()
		.get("/zonas?organizacion_id=0")
		.then()
		.statusCode(200)
		.body("data", hasSize(0))
		;
	}
	
	@Test
	void testWithInvalidFilterOrganizacion() {
		RestAssured.given()
		.get("/zonas?organizacion_id=a")
		.then()
		.statusCode(400)
		;
	}	

}
