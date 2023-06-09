package org.circle8.controller.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TokenResponse implements ApiResponse {
	public String token;
	public String refreshToken;
	public UserResponse user;
}
