package org.circle8.service;

import org.circle8.dao.ZonaDao;
import org.circle8.dto.ZonaDto;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.ZonaFilter;

import com.google.inject.Inject;
import lombok.val;

public class ZonaService {

	private final ZonaDao dao;
	
	@Inject
	public ZonaService(ZonaDao dao) {
		this.dao = dao;
	}
	
	public ZonaDto get(ZonaFilter f) throws ServiceException {
		try ( val t = dao.open(true) ) {
			val zona = this.dao.get(t,f)
					.orElseThrow(() -> new NotFoundException("No existe la zona"));
			return ZonaDto.from(zona);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la zona", e);
		}
	}
}
