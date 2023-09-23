package org.circle8.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dao.ConsejoDao;
import org.circle8.dto.ConsejoDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.update.UpdateConsejo;

import java.util.List;

@Singleton
public class ConsejoService {
	private final ConsejoDao dao;

	@Inject
	public ConsejoService(ConsejoDao dao) { this.dao = dao; }

	public List<ConsejoDto> list() throws ServiceException {
		try {
			return this.dao.list().stream().map(ConsejoDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de consejos", e);
		}
	}

	public ConsejoDto save(ConsejoDto c) throws ServiceException {
		try {
			return ConsejoDto.from(this.dao.save(c.toEntity()));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el consejo", e);
		}
	}

	public void update(UpdateConsejo update) throws ServiceException {
		try {
			this.dao.update(update);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el consejo", e);
		}
	}

	public void delete(long id) throws ServiceException {
		try {
			this.dao.delete(id);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el consejo", e);
		}
	}
}
