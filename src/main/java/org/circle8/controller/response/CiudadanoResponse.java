package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CiudadanoResponse implements ApiResponse {
	public long id;
	public String username;
	public String nombre;
}
