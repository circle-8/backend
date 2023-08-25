package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dao.UserDao;
import org.circle8.dto.UserDto;
import org.circle8.entity.Organizacion;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

@Singleton
public class UserService {
	private final UserDao dao;
	private final CryptService crypt;
	private final SuscripcionService suscripcion;
	private final CiudadanoService ciudadano;
	private final RecicladorUrbanoService reciclador;
	private final TransportistaService transportista;
	private final OrganizacionService organizacion;

	@Inject
	public UserService(
		UserDao dao,
		CryptService crypt,
		SuscripcionService suscripcion,
		CiudadanoService ciudadano,
		RecicladorUrbanoService reciclador,
		TransportistaService transportista,
		OrganizacionService organizacion
	) {
		this.dao = dao;
		this.crypt = crypt;
		this.suscripcion = suscripcion;
		this.ciudadano = ciudadano;
		this.reciclador = reciclador;
		this.transportista = transportista;
		this.organizacion = organizacion;
	}

	public UserDto login(String username, String password) throws ServiceException {
		try {
			var u = this.dao.get(username).orElseThrow(() -> new NotFoundException("El usuario y/o contraseña son incorrectos"));
			if ( !crypt.check(password, u.hashedPassword) )
				throw new NotFoundException("El usuario y/o contraseña son incorrectos");

			return UserDto.from(u);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener el usuario", e);
		}
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
			user.suscripcion = suscripcion.subscribe(t, user);

			user = dao.save(t, user);
			dto.id = user.id;

			switch ( user.tipo ) {
				case CIUDADANO -> ciudadano.save(t, user);
				case TRANSPORTISTA -> {
					var c = ciudadano.save(t, user);
					transportista.save(t, c);
				}
				case RECICLADOR_URBANO -> user.recicladorUrbanoId = reciclador.save(t, user);
				case ORGANIZACION -> {
					var o = new Organizacion(user.id);
					o.razonSocial = dto.razonSocial;
					organizacion.save(t, o);
					user.organizacionId = o.id;
				}
				default -> throw new IllegalStateException("hay un TipoUsuario no contemplado al guardar");
			}

			t.commit();
		} catch ( DuplicatedEntry e ) {
			throw new ServiceException("El usuario y/o email ya se encuentran registrados", e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el usuario", e);
		}

		return dto;
	}
}
