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
class SignUpTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();

	@Test
	void testNewCiudadano() throws Exception {
		var request = """
   {
     "username": "nuevo-usuario",
     "password": "1234",
     "nombre": "Nuevo Usuario",
     "email": "nuevo@email.com",
     "tipoUsuario": "CIUDADANO"
   }""";

		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario"))
			.body("nombre", equalTo("Nuevo Usuario"))
			.body("email", equalTo("nuevo@email.com"))
			.body("tipoUsuario", equalTo("CIUDADANO"))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;

		var checkUserSQL = "SELECT \"Username\" FROM public.\"Usuario\" WHERE \"Username\" = ? AND \"Email\" = ?";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkUserSQL) ) {
			ps.setString(1, "nuevo-usuario");
			ps.setString(2, "nuevo@email.com");
			assertTrue(ps.executeQuery().next());
		}
	}

	@Test
	void testNewReciclador() throws Exception {
		var request = """
   {
     "username": "nuevo-usuario-reciclador",
     "password": "1234",
     "nombre": "Nuevo Usuario reciclador",
     "email": "nuevoReciclador@email.com",
     "tipoUsuario": "RECICLADOR_URBANO",
     "organizacionId": 1
   }""";

		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario-reciclador"))
			.body("nombre", equalTo("Nuevo Usuario reciclador"))
			.body("email", equalTo("nuevoReciclador@email.com"))
			.body("tipoUsuario", equalTo("RECICLADOR_URBANO"))
			.body("ciudadanoId", nullValue())
			.body("recicladorUrbanoId", is(not(empty())))
			.body("organizacionId", equalTo(1))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
			;

		var checkUserSQL = "SELECT \"Username\" FROM public.\"Usuario\" WHERE \"Username\" = ? AND \"Email\" = ?";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkUserSQL) ) {
			ps.setString(1, "nuevo-usuario-reciclador");
			ps.setString(2, "nuevoReciclador@email.com");
			assertTrue(ps.executeQuery().next());
		}
		
		
		/*testNewRecicladorWithExtraInfo*/		
		
		var requestExtraInfo = """
   {
     "username": "nuevo-usuario-reciclador-extra-info",
     "password": "1234",
     "nombre": "Nuevo Usuario reciclador",
     "email": "nuevoRecicladorExtraInfo@email.com",
     "tipoUsuario": "RECICLADOR_URBANO",
     "organizacionId": 1,
     "reciclador": {
       "domicilio": "Somewhere over the rainbow 123",
       "fechaNacimiento": "1980-09-12"
     }
   }""";

		RestAssured.given()
			.body(requestExtraInfo)
			.post("/user")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("username", equalTo("nuevo-usuario-reciclador-extra-info"))
			.body("nombre", equalTo("Nuevo Usuario reciclador"))
			.body("email", equalTo("nuevoRecicladorExtraInfo@email.com"))
			.body("tipoUsuario", equalTo("RECICLADOR_URBANO"))
			.body("ciudadanoId", nullValue())
			.body("recicladorUrbanoId", is(not(empty())))
			.body("organizacionId", equalTo(1))
			.body("password", is(nullValue()))
			.body("suscripcion", is(not(empty())))
		;

		var checkUserSQL2 = "SELECT \"Username\" FROM public.\"Usuario\" WHERE \"Username\" = ? AND \"Email\" = ?";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkUserSQL2) ) {
			ps.setString(1, "nuevo-usuario-reciclador-extra-info");
			ps.setString(2, "nuevoRecicladorExtraInfo@email.com");
			assertTrue(ps.executeQuery().next());
		}
		
		/*testNewRecicladorSuperaLimite*/
		RestAssured.given()
		.body(request)
		.post("/user")
		.then()
		.statusCode(400)
		.body("message", stringContainsInOrder("alcanzado el limite", "recicladores"))
		;		
	}

	@Test
	void testNewOrganizacion() throws Exception {
		var request = """
   {
     "username": "nueva-organizacion",
     "password": "1234",
     "nombre": "Nueva organizacion",
     "email": "nueva-organizacion@email.com",
     "tipoUsuario": "ORGANIZACION",
     "razonSocial": "Organizacion"
   }""";

		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(200)
			.body("id", is(not(emptyOrNullString())))
			.body("suscripcion", is(not(empty())))
		;

		var checkUserSQL = """
		SELECT "Username" FROM public."Usuario" WHERE "Username" = ? AND "Email" = ? AND "SuscripcionId" IS NOT NULL
		""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkUserSQL) ) {
			ps.setString(1, "nueva-organizacion");
			ps.setString(2, "nueva-organizacion@email.com");
			assertTrue(ps.executeQuery().next());
		}
	}

	@Test
	void testExistingUsername() {
		var request = """
  {
    "username": "existing",
    "password": "1234",
    "nombre": "Nuevo Usuario",
    "email": "nuevo@email.com",
    "tipoUsuario": "CIUDADANO"
  }""";
		RestAssured.given()
			.body(request)
			.post("/user")
			.then()
			.statusCode(400)
			.body("code", equalTo("BAD_REQUEST"))
			.body("message", stringContainsInOrder("usuario", "registrado"))
			;
	}

	@Test
	void testExistingEmail() {
		var request = """
  {
    "username": "nuevo",
    "password": "1234",
    "nombre": "Nuevo Usuario",
    "email": "existing@email.com",
    "tipoUsuario": "CIUDADANO"
  }""";
		RestAssured.given()
			.body(request)
			.post("/user")
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
			.then()
			.statusCode(400)
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
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
			.post("/user")
			.then()
			.statusCode(400)
		;
	}

}
