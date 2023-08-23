package org.circle8.integration.user;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sql.DataSource;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class UserPutTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();

	@Test
	void testPutOk() throws Exception {
		var request = """
				{
				  "username": "nuevo-usuario-1",
				  "password": "1234",
				  "nombre": "Nuevo Usuario 1",
				  "email": "nuevo@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario-1"))
			.body("nombre", equalTo("Nuevo Usuario 1"))
			.body("email", equalTo("nuevo@email.com"))
			.body("tipoUsuario", equalTo("CIUDADANO"))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;
	}
	
	@Test
	void testPutTransportistaOk() throws Exception {
		var request = """
				{
				  "username": "nuevo-usuario-1",
				  "password": "1234",
				  "nombre": "Nuevo Usuario 1",
				  "email": "nuevo@email.com",
				  "tipoUsuario": "TRANSPORTISTA"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario-1"))
			.body("nombre", equalTo("Nuevo Usuario 1"))
			.body("email", equalTo("nuevo@email.com"))
			.body("tipoUsuario", equalTo("TRANSPORTISTA"))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;
		
		var checkTransportistaSQL = """
				SELECT
				t."ID", t."UsuarioId", t."Polyline"
			  FROM "Transportista" AS t
			 WHERE "UsuarioId" = ?
			""";
		try ( var conn = ds.getConnection();
				var ps = conn.prepareStatement(checkTransportistaSQL) ) {
			ps.setLong(1, 1);
			assertTrue(ps.executeQuery().next());
		}
	}
	
	
	@Test
	void testExistUserName() throws Exception {
		var request = """
				{
				  "username": "username2",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "nuevo@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
			.body("message", stringContainsInOrder("usuario", "registrado"))
			;
	}
	
	@Test
	void testExistEmail() throws Exception {
		var request = """
				{
				  "username": "nuevo-usuario-1",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing2@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
			.body("message", stringContainsInOrder("email", "registrado"))
			;
	}
	
	@Test
	void testWithOutUserName() {
		var request = """
				{
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}


	@Test
	void testWithOutPassword() {
		var request = """
				{
				  "username": "nuevo",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutNombre() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutEmail() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "tipoUsuario": "CIUDADANO"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutTipo() {
		var request = """
				{
				   "username": "nuevo",
				   "password": "1234",
				   "nombre": "Nuevo Usuario",
				   "email": "existing@email.com"
				 }""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(500)
		;
	}
	
	@Test
	void testWithOutRazonSocial() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "ORGANIZACION"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testInvalidTipoPorRazonSocial() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO",
				  "razonSocial": "RSA",
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testWithOutOrganizacionId() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "RECICLADOR_URBANO"
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testInvalidTipoPorOrganizacionId() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO",
				  "organizacionId": 1,
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}

	@Test
	void testInvalidPorTipoZonaId() {
		var request = """
				{
				  "username": "nuevo",
				  "password": "1234",
				  "nombre": "Nuevo Usuario",
				  "email": "existing@email.com",
				  "tipoUsuario": "CIUDADANO",
				  "zonaId": 1,
				}""";
		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}
}
