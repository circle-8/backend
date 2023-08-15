package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.RecorridoDto;
import org.circle8.entity.Recorrido;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;

public class RecorridoService {
	private final RecorridoDao dao;

	@Inject
	public RecorridoService(RecorridoDao dao) { this.dao = dao; }

	public RecorridoDto get(long id, RecorridoExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return RecorridoDto.from(get(t, id, x));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el recorrido", e);
		}
	}

	private Recorrido get(Transaction t, long id, RecorridoExpand x) throws ServiceException {
		try {
			return this.dao.get(t, id, x)
				.orElseThrow(() -> new NotFoundException("No existe la solicitud"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el recorrido", e);
		}
	}
}
