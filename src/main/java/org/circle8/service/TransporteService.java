package org.circle8.service;

import org.circle8.dao.Transaction;
import org.circle8.dao.TransporteDao;
import org.circle8.dto.TransporteDto;
import org.circle8.entity.Transporte;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransporteExpand;
import lombok.val;
import com.google.inject.Inject;

public class TransporteService {

	private final TransporteDao dao;

	@Inject
	public TransporteService(TransporteDao dao) {
		this.dao = dao;
	}
	
	public TransporteDto get(long id, TransporteExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return TransporteDto.from(get(t, id, x));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}	
	
	private Transporte get(Transaction t, long id, TransporteExpand x) throws ServiceException {
		try {
			return this.dao.get(t, id, x)
				.orElseThrow(() -> new NotFoundException("No existe el transporte"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el transporte", e);
		}
	}
}
