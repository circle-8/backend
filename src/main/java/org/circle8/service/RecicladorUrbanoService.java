package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.RecicladorUrbanoDao;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.Transaction;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.InequalityFilter;
import org.circle8.filter.RecorridoFilter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class RecicladorUrbanoService {
	private final RecicladorUrbanoDao dao;
	private final RecorridoDao recorridoDao;

	@Inject
	public RecicladorUrbanoService(RecicladorUrbanoDao dao, RecorridoDao recorridoDao) {
		this.dao = dao;
		this.recorridoDao = recorridoDao;
	}

	User save(Transaction t, User u) throws ServiceException {
		// TODO: esto deberia devolver un RecicladorUrbano :(
		try {
			u.recicladorUrbanoId = dao.save(t, u);
			return u;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el reciclador", e);
		}
	}

	void update(Transaction t, User u) throws ServiceException {
		try {
			dao.update(t, u);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el reciclador", e);
		}
	}

	public void removeZona(long recicladorId) throws ServiceException {
		try ( val t = dao.open(true) ){
			// Traer todos los recorridos proximos
			var nextFilter = RecorridoFilter.builder()
				.recicladorId(recicladorId)
				.fechaRetiro(InequalityFilter.<LocalDate>builder().gt(LocalDate.now()).build())
				.build();
			var nextRecorridos = recorridoDao.list(t, nextFilter, RecorridoExpand.EMPTY);
			if ( !nextRecorridos.isEmpty() ) {
				throw new ServiceException("El reciclador tiene recorridos pendientes");
			}

			// Traer todos los recorridos activos
			var activeFilter = RecorridoFilter.builder()
				.recicladorId(recicladorId)
				.fechaRetiro(InequalityFilter.<LocalDate>builder().equal(LocalDate.now()).build())
				.fechaInicio(InequalityFilter.<ZonedDateTime>builder().isNull(false).build())
				.fechaFin(InequalityFilter.<ZonedDateTime>builder().isNull(true).build())
				.build();
			var activeRecorridos = recorridoDao.list(t, activeFilter, RecorridoExpand.EMPTY);
			if ( !activeRecorridos.isEmpty() ) {
				throw new ServiceException("El reciclador tiene recorridos activos para hoy");
			}

			// Desasociar zona del reciclador
			dao.desasociarZona(t, recicladorId, null);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al dar de baja el reciclador", e);
		}
	}
}
