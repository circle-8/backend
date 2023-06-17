package org.circle8.controller.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse implements ApiResponse {
	public String token;
	public String refreshToken;
	public UserResponse user;
}
