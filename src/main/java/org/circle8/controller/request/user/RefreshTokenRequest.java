package org.circle8.controller.request.user;

import com.google.common.base.Strings;
import lombok.val;
import org.circle8.controller.request.IRequest;

public class RefreshTokenRequest implements IRequest {
	public String refreshToken;
	public String accessToken;

	@Override
	public Validation valid() {
		// Solo se debe utilizar el valid de RefreshToken si en las cookies no se encuentran los
		// tokens buscados. En ese caso, la request puede venir vacia

		val v = new Validation();
		if ( Strings.isNullOrEmpty(refreshToken) )
			v.add("Falta 'refreshToken'");
		if ( Strings.isNullOrEmpty(accessToken) )
			v.add("Falta 'accessToken'");

		return v;
	}
}
