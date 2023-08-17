package org.circle8.integration.residuo;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ApiTestExtension.class)
class ResiduoDeleteReciclajeTest {
	@Test
	void testResiduoFulfilledNotRecorrido() {
		RestAssured.given()
			.delete("/residuo/3/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("no es parte de un recorrido"))
		;
	}

	@Test
	void testResiduoFulfilledRecorrido() {
		RestAssured.given()
			.delete("/residuo/13/reciclaje")
			.then()
			.statusCode(400)
			.body("message", containsString("ya ha sido retirado"))
		;
	}

	@Test
	void testDeleteResiduoFromRecorridoOK() throws Exception {
		RestAssured.given()
			.delete("/residuo/5/reciclaje")
			.then()
			.statusCode(200)
		;

		var ds = ApiTestExtension.Dep.getDatasource();
		var checkDeleteRecorrido = "SELECT \"RecorridoId\" FROM public.\"Residuo\" WHERE \"ID\" = ?";
		try ( var conn = ds.getConnection(); var ps = conn.prepareStatement(checkDeleteRecorrido) ) {
			ps.setLong(1, 5);

			var rs = ps.executeQuery();
			assertTrue(rs.next());
			assertNull(rs.getObject("RecorridoId"));
		}
	}
}
