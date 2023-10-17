package org.circle8.integration.transaccion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.restassured.RestAssured;

@ExtendWith(ApiTestExtension.class)
public class TransaccionRemoveTransporteTest {
	private final DataSource ds = ApiTestExtension.Dep.getDatasource();
	
	@Test
	void testDeleteOk() throws SQLException {
		RestAssured.given()
					  .delete("/transaccion/5/transporte/")
					  .then()
					  .statusCode(200)
		;
		
		
		var checkDelete = """
					SELECT
					t."ID"
				  FROM "Transporte" AS t
				 WHERE t."ID" = ?
				""";
		try (var conn = ds.getConnection(); 
				var ps = conn.prepareStatement(checkDelete)) {
			ps.setLong(1, 3);
			var rs = ps.executeQuery();
			assertFalse(rs.next());
		}
	}
	
	@Test
	void testWithOutTransporte() {
		RestAssured.given()
					  .delete("/transaccion/4/transporte/")
					  .then()
					  .statusCode(400)
		;
	}
	
	@Test
	void testWithTransportista() {
		RestAssured.given()
					  .delete("/transaccion/1/transporte/")
					  .then()
					  .statusCode(400)
					  .body("code", equalTo("BAD_REQUEST"))
					  .body("message", stringContainsInOrder("transporte", "transportista"))
		;
	}
	

	@Test
	void testInvalidTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/1a2/transporte/")
					  .then()
					  .statusCode(400)
		;
	}
	

	@Test
	void testWhitoutTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion//transporte/")
					  .then()
					  .statusCode(404)
		;
	}

	
	@Test
	void testWhithInexistingTransaccionId() {
		RestAssured.given()
					  .delete("/transaccion/0/transporte/")
					  .then()
					  .statusCode(404)
		;
	}
}
