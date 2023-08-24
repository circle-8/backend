package org.circle8.integration.zona;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class ZonaDeleteTest {
	
	@Test
	void testDeleteOk() throws Exception {
		RestAssured.given()
		.delete("/organizacion/1/zona/1")
		.then()
		.statusCode(200)
		;
		
		var ds = ApiTestExtension.Dep.getDatasource();
		
		var checkDeleteTipos = """
				SELECT "ZonaId"
				FROM public."Zona_TipoResiduo"
				WHERE "ZonaId" = ?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkDeleteTipos) ) {
			ps.setLong(1, 1);
			var rs = ps.executeQuery();
			assertFalse(rs.next());
		}
		
		var checkDeletePuntos = """
				SELECT "ZonaId"
				FROM public."PuntoResiduo_Zona"
				WHERE "ZonaId" = ?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkDeletePuntos) ) {
			ps.setLong(1, 1);
			var rs = ps.executeQuery();
			assertFalse(rs.next());
		}
		
		var checkDeleteZonaInReciclador = """
				SELECT "ZonaId"
				FROM public."RecicladorUrbano"
				WHERE "ZonaId" = ?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkDeleteZonaInReciclador) ) {
			ps.setLong(1, 1);
			var rs = ps.executeQuery();
			assertFalse(rs.next());
		}
		
		var checkDeleteZonaInRecorrido = """
				SELECT "ZonaId"
				FROM public."Recorrido"
				WHERE "ZonaId" = ?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkDeleteZonaInRecorrido) ) {
			ps.setLong(1, 1);
			var rs = ps.executeQuery();
			assertFalse(rs.next());
		}
	}

	@Test
	void testNotFound() {
		RestAssured.given()
		.delete("/organizacion/0/zona/0")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutOrganizacionId() {
		RestAssured.given()
		.delete("/organizacion//zona/1")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testWithOutZonaId() {
		RestAssured.given()
		.delete("/organizacion/1/zona/")
		.then()
		.statusCode(404)
		;
	}

	@Test
	void testInvalidOrganizacionId() {
		RestAssured.given()
		.delete("/organizacion/aa/zona/1")
		.then()
		.statusCode(400)
		;
	}

	@Test
	void testInvalidZonaId() {
		RestAssured.given()
		.delete("/organizacion/1/zona/aa")
		.then()
		.statusCode(400)
		;
	}
}
