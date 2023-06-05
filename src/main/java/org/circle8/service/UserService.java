package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dao.UserDao;
import org.circle8.dto.UserDto;

@Singleton
public class UserService {
	private final UserDao dao;
	private final CryptService crypt;

	@Inject
	public UserService(UserDao dao, CryptService crypt) {
		this.dao = dao;
		this.crypt = crypt;
	}

	/**
	 * Guarda el nuevo usuario.
	 * @param dto user dto que luego sirve como return
	 * @param password password sin hash
	 * @return el mismo user que se recibió como parámetro, pero modificado
	 */
	public UserDto save(UserDto dto, String password) {
		var user = dto.toEntity();
		user.hashedPassword = crypt.hash(password);
		dao.save(user);

		dto.id = user.id;
		return dto;
	}
}
