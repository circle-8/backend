package org.circle8.service;

import java.security.Provider;

import org.circle8.dao.TransaccionDao;
import org.circle8.dto.TransaccionDto;
import org.circle8.entity.Transaccion;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

import com.google.inject.Inject;

public class TransaccionService {
	private final TransaccionDao dao;

	@Inject
	public TransaccionService(TransaccionDao dao) {
		this.dao = dao;
	}

	public TransaccionDto get(Long transaccionId) throws ServiceException {
		try{
			return this.dao.get(transaccionId)
				.map(TransaccionDto::from)
								.orElseThrow(() -> new NotFoundException("No existe el punto de residuos"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el punto de reciclaje", e);
		}
	}

}
