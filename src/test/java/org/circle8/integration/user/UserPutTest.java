package org.circle8.integration.user;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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
			.body("email", is(nullValue()))
			;
		
//		var checkEmailSQL = """
//				SELECT
//				u."Email"
//			  FROM "Usuario" AS u
//			 WHERE u."ID" = ?
//			""";
//		try ( var conn = ds.getConnection();
//				var ps = conn.prepareStatement(checkEmailSQL) ) {
//			ps.setLong(1, 1);
//			assertTrue(ps.executeQuery().next());
//			assertTrue(rs.getString("NombreApellido"));
//		}
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
				  "organizacionId": 1
				}""";

		RestAssured.given()
			.body(request)
			.put("/user/3")
			.then()
			.statusCode(200)
			.body("organizacionId", equalTo(1))
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
