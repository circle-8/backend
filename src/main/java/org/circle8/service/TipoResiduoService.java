package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.dao.TipoResiduoDao;
import org.circle8.dto.TipoResiduoDto;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;

import java.util.List;

public class TipoResiduoService {

	private final TipoResiduoDao dao;

	@Inject
	public TipoResiduoService(TipoResiduoDao dao){
		this.dao = dao;
	}

	public List<TipoResiduoDto> list() throws ServiceError{
		try {
			return this.dao.list().stream().map(TipoResiduoDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de tipos de residuo", e);
		}
	}

}
