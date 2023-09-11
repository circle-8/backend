package org.circle8.integration.user;

import io.restassured.RestAssured;
import org.circle8.ApiTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

@ExtendWith(ApiTestExtension.class)
class UserListTest {
	@Test
	void testGetOk() {
		RestAssured.given()
			.get("/users?tipo_usuario=RECICLADOR_URBANO&organizacion_id=1")
			.then()
			.statusCode(200)
			.body("data.organizacionId", everyItem(equalTo(1)))
			.body("data.tipoUsuario", everyItem(equalTo("RECICLADOR_URBANO")))
		;
	}
}
