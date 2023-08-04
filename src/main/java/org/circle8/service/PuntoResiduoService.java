package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoResiduoDao;
import org.circle8.dto.PuntoResiduoDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.filter.PuntoResiduoFilter;

import com.google.inject.Inject;

public class PuntoResiduoService {
	private final PuntoResiduoDao dao;

	@Inject
	public PuntoResiduoService(PuntoResiduoDao puntoResiduoDao) {
		this.dao = puntoResiduoDao;
	}

	public PuntoResiduoDto save(PuntoResiduoDto dto) throws ServiceError, NotFoundException {
		var punto = dto.toEntity();
		try( var t = dao.open(true)) {
			punto = dao.save(t, punto);
			dto.id = punto.id;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de residuo", e);
		}
		return dto;
	}

	public PuntoResiduoDto put(PuntoResiduoDto dto) throws ServiceError, NotFoundException {
		try( var t = dao.open(true)) {
			var punto = dao.put(t, dto.toEntity());
			return PuntoResiduoDto.from(punto);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el punto de residuo", e);
		}
	}

	public List<PuntoResiduoDto> list(PuntoResiduoFilter f, PuntoResiduoExpand x) throws ServiceException {
		try {
			return this.dao.list(f, x).stream().map(PuntoResiduoDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener los puntos de residuo", e);
		}
	}

	public PuntoResiduoDto get(
		Long ciudadanoId,
		Long id,
		PuntoResiduoExpand x
	) throws ServiceException {
		try {
			return this.dao.get(ciudadanoId, id, x)
				.map(PuntoResiduoDto::from)
				.orElseThrow(() -> new NotFoundException("No existe el punto de residuos"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener el punto de residuo", e);
		}
	}
}
