package org.circle8.service;

import lombok.val;
import org.circle8.dao.ResiduoDao;
import org.circle8.dto.ResiduoDto;
import org.circle8.exception.ForeingKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;

import com.google.inject.Inject;
import org.circle8.exception.ServiceException;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;

public class ResiduoService {
	private final ResiduoDao dao;

	@Inject
	public ResiduoService(ResiduoDao dao) {
		this.dao = dao;
	}

	public ResiduoDto save(ResiduoDto dto) throws ServiceException {
		try( var t = dao.open(true)) {
			var residuo = dao.save(t, dto.toEntity());
			dto.id = residuo.id;
			return dto;
		} catch ( ForeingKeyException e ) {
			throw new NotFoundException(e.getMessage());
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el residuo", e);
		}
	}

	public ResiduoDto fulfill(long residuoId) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = dao.get(t, residuoId).orElseThrow(() -> new NotFoundException("El residuo no existe"));

			if ( r.fechaRetiro != null )
				throw new ServiceException("El residuo ya ha sido retirado previamente");

			r.fechaRetiro = ZonedDateTime.now(Dates.UTC);
			dao.update(t, r);

			return ResiduoDto.from(r);
		} catch ( ForeingKeyException e ) {
			throw new NotFoundException(e.getMessage());
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el residuo", e);
		}
	}
}
