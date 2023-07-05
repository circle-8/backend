package org.circle8.service;

import com.google.inject.Inject;
import org.circle8.controller.response.PuntoResiduoResponse;
import org.circle8.dao.PuntoResiduoDao;
import org.circle8.dto.PuntoResiduoDto;
import org.circle8.entity.PuntoResiduo;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.PuntoResiduoFilter;

import java.util.List;

public class PuntoResiduoService {
	private final PuntoResiduoDao dao;

	@Inject
	public PuntoResiduoService(PuntoResiduoDao puntoResiduoDao) {
		this.dao = puntoResiduoDao;
	}

	public List<PuntoResiduoDto> list(PuntoResiduoFilter f) throws ServiceException {
		try {
			return this.dao.list(f).stream().map(PuntoResiduoDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener los puntos de residuo", e);
		}
	}
}
