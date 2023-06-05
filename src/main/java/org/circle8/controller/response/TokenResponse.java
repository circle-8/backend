package org.circle8.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponse implements ApiResponse {
	public String token;
	public UserResponse user;
}
