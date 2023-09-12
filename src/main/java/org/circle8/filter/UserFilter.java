package org.circle8.filter;

import lombok.Builder;
import org.circle8.dto.TipoUsuario;

@Builder
public class UserFilter {
	public Long id;
	public String username;
	public Long organizacionId;
	public TipoUsuario tipoUsuario;
}
