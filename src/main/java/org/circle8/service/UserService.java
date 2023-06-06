package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dao.UserDao;
import org.circle8.dto.SuscripcionDto;
import org.circle8.dto.UserDto;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

@Singleton
public class UserService {
	private final UserDao dao;
	private final CryptService crypt;
	private final SuscripcionService suscripcion;

	@Inject
	public UserService(UserDao dao, CryptService crypt, SuscripcionService suscripcion) {
		this.dao = dao;
		this.crypt = crypt;
		this.suscripcion = suscripcion;
	}

	/**
	 * Guarda el nuevo usuario.
	 * @param dto user dto que luego sirve como return
	 * @param password password sin hash
	 * @return el mismo user que se recibió como parámetro, pero modificado
	 */
	public UserDto save(UserDto dto, String password) throws ServiceException {
		var user = dto.toEntity();
		user.hashedPassword = crypt.hash(password);

		try ( var t = dao.open() ) {
			user = dao.save(t, user);
			dto.id = user.id;

			var s = suscripcion.subscribe(t, user);
			dto.suscripcion = SuscripcionDto.from(s);

			t.commit();
		} catch ( DuplicatedEntry e ) {
			throw new ServiceException(String.format("El usuario \"%s\" ya existe", dto.username), e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el usuario", e);
		}

		return dto;
	}
}
