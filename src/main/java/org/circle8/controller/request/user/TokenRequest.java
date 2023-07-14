package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import org.circle8.controller.request.IRequest;

public class TokenRequest implements IRequest {
	public String username;
	public String password;

	@Override
	public Validation valid() {
		final var v = new Validation();
		if ( Strings.isNullOrEmpty(username) )
			v.add("falta 'username'");
		if ( Strings.isNullOrEmpty(password) )
			v.add("falta 'password'");

		return v;
	}
}
