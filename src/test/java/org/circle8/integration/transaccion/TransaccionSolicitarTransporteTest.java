package org.circle8.integration.transaccion;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransaccionSolicitarTransporteTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();
	
	@Test
	void testSolicitarOk() throws SQLException {
		RestAssured.given()
			.post("/transaccion/4/transporte")
			.then()
			.statusCode(200)
		;
		
		var checkTransporte = """
				SELECT "PrecioSugerido"
				FROM public."Transporte"
				WHERE "ID"=?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkTransporte) ) {
			ps.setLong(1, 3);
			var rs = ps.executeQuery();
			assertTrue(rs.next());
			assertNotNull(rs.getObject("PrecioSugerido"));
		}
		
		var checkTransaccion = """
				SELECT "TransporteId"
				FROM public."TransaccionResiduo"
				WHERE "ID"=?;
				""";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkTransaccion) ) {
			ps.setLong(1, 4);
			var rs = ps.executeQuery();
			assertTrue(rs.next());
			assertNotNull(rs.getObject("TransporteId"));
		}
	}
	
	@Test
	void testNotFound() {
		RestAssured.given()
			.post("/transaccion/0/transporte")
			.then()
			.statusCode(404)
		;
	}

	
	@Test
	void testAlreadyHaveTransporte() {
		RestAssured.given()
			.post("/transaccion/1/transporte")
			.then()
			.statusCode(400)
		;
	}
	
	@Test
	void testWhitoutId() {
		RestAssured.given()
			.post("/transaccion//transporte")
			.then()
			.statusCode(404)
		;
	}
	
	@Test
	void testInvalidId() {
		RestAssured.given()
			.post("/transaccion/aa/transporte")
			.then()
			.statusCode(400)
		;
	}
}
