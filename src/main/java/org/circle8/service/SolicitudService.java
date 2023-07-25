package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.SolicitudDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.SolicitudDto;
import org.circle8.entity.Solicitud;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;

public class SolicitudService {
	private final SolicitudDao dao;

	public enum TipoSolicitud {
		DEPOSITO,
		RETIRO;

		public boolean isRetiro() { return this == RETIRO; }
	}

	@Inject
	public SolicitudService(SolicitudDao dao) { this.dao = dao; }

	public SolicitudDto save(long residuoId, long puntoReciclaje, TipoSolicitud tipo) throws ServiceException {
		try ( val t = dao.open(true) ) {
			val id = dao.save(t, residuoId, puntoReciclaje, tipo);
			// TODO: validacion de que el punto de reciclaje acepte ese tipo de residuo
			// TODO: validacion que el residuo y el punto de reciclaje no pertenezcan al mismo user
			return SolicitudDto.from(get(t, id));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar la solicitud", e);
		}
	}

	private Solicitud get(Transaction t, long id) throws ServiceException {
		try {
			return this.dao.get(t, id)
				.orElseThrow(() -> new NotFoundException("No existe la solicitud"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}
}
