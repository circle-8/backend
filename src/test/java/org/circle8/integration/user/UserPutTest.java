package org.circle8.integration.user;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApiTestExtension.class)
public class UserPutTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();

	@Test
	void testPutUsernameOk() throws Exception {
		var request = """
				{
				  "username": "nuevo-usuario-1"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("username", equalTo("nuevo-usuario-1"))
			;
	}

	@Test
	void testExistingUsername() throws Exception {
		var request = """
				{
				  "username": "username2"
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
	void testPutNombreOk() throws Exception {
		var request = """
				{
				  "nombre": "Nuevo Usuario 1"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("nombre", equalTo("Nuevo Usuario 1"))
			;
	}

	@Test
	void testPutEmailOk() throws Exception {
		var request = """
				{
				  "email": "nuevo@email.com"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("email", equalTo("nuevo@email.com"))
			;

		var checkEmailSQL = """
				SELECT
				u."Email"
			  FROM "Usuario" AS u
			 WHERE u."ID" = ?
			""";
		try ( var conn = ds.getConnection();
				var ps = conn.prepareStatement(checkEmailSQL) ) {
			ps.setLong(1, 1);
			var rs = ps.executeQuery();
			assertTrue(rs.next());
			assertTrue(rs.getString("Email").equals("nuevo@email.com"));
		}
	}


	@Test
	void testExistingEmail() throws Exception {
		var request = """
				{
				  "email": "existing2@email.com"
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
	void testPutTransportistaOk() throws Exception {
		var request = """
				{
				  "tipoUsuario": "TRANSPORTISTA"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/1")
			.then()
			.statusCode(200)
			.body("tipoUsuario", equalTo("TRANSPORTISTA"))
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
	void testPutOrganizacionOk() throws Exception {
		var request = """
				{
				  "organizacionId": 2
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/3")
			.then()
			.statusCode(200)
			.body("organizacionId", equalTo(2))
			;
	}

	@Test
	void testPutZonaOk() throws Exception {
		var request = """
				{
				 "zonaId": 3
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/3")
			.then()
			.statusCode(200)
			.body("zonaId", equalTo(3))
			;
	}

	@Test
	void testPutDatosReciclador() throws Exception {
		var request = """
				{
				 "reciclador": {
				   "fechaNacimiento": "1980-09-12",
				   "dni": "40123456"
				 }
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/3")
			.then()
			.statusCode(200)
			.body("reciclador.fechaNacimiento", equalTo("1980-09-12"))
			.body("reciclador.dni", equalTo("40123456"))
		;
	}

	@Test
	void testPutRazonSocialOk() throws Exception {
		var request = """
				{
				 "razonSocial": "Organizacion 1 S.A"
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/6")
			.then()
			.statusCode(200)
			;

		var checkRazonSocialSQL = """
				SELECT
				o."RazonSocial"
			  FROM "Organizacion" AS o
			 WHERE o."UsuarioId" = ?
			""";
		try ( var conn = ds.getConnection();
				var ps = conn.prepareStatement(checkRazonSocialSQL) ) {
			ps.setLong(1, 6);
			var rs = ps.executeQuery();
			assertTrue(rs.next());
			assertTrue(rs.getString("RazonSocial").equals("Organizacion 1 S.A"));
		}
	}


	@Test
	void testWithOutRequest() {
		RestAssured.given()
			.put("/user/1")
			.then()
			.statusCode(500)
		;
	}

	@Test
	void testEmptyRequest() {
		RestAssured.given()
			.body("{}")
			.put("/user/1")
			.then()
			.statusCode(400)
		;
	}
}
