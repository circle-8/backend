package org.circle8.service;

import java.util.List;

import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dao.ResiduoDao;
import org.circle8.dao.SolicitudDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.SolicitudDto;
import org.circle8.entity.Solicitud;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.filter.SolicitudFilter;

import com.google.inject.Inject;

import lombok.val;

public class SolicitudService {
	private final SolicitudDao dao;
	private final PuntoReciclajeDao puntoDao;
	private final ResiduoDao residuoDao;

	public enum TipoSolicitud {
		DEPOSITO,
		RETIRO;

		public boolean isRetiro() { return this == RETIRO; }
	}

	@Inject
	public SolicitudService(
		SolicitudDao dao,
		PuntoReciclajeDao puntoDao,
		ResiduoDao residuoDao
	) {
		this.dao = dao;
		this.puntoDao = puntoDao;
		this.residuoDao = residuoDao;
	}

	public SolicitudDto save(long residuoId, long puntoReciclajeId, TipoSolicitud tipo) throws ServiceException {
		try ( val t = dao.open(true) ) {
			// TODO: validacion extra (nice to have): que no se pueda crear una solicitud de DEPOSITO cuando hay una de RETIRO igual

			val residuo = residuoDao.get(t, residuoId)
				.orElseThrow(() -> new NotFoundException("No existe el residuo"));

			if ( residuo.fechaRetiro != null )
				throw new ServiceException("El residuo ya se ha retirado");

			val punto = puntoDao.get(t, puntoReciclajeId)
				.orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje"));

			/* Validar que el punto de reciclaje acepte el tipo de residuo */
			val acceptsTipo = punto.tipoResiduo.stream().anyMatch(tr -> tr.id == residuo.tipoResiduo.id);
			if ( !acceptsTipo )
				throw new ServiceException("El Punto de Reciclaje no admite este tipo de residuo");

			if ( punto.reciclador.ciudadanoId.equals(residuo.ciudadanoId) )
				throw new ServiceException("El residuo no puede pertenecer al dueño del punto");

			val id = dao.save(t, residuoId, puntoReciclajeId, tipo);

			return SolicitudDto.from(get(t, id));
		} catch ( DuplicatedEntry e ) {
			throw new ServiceException("Solicitud ya existente", e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar la solicitud", e);
		}
	}
	
	public List<SolicitudDto> list(SolicitudFilter filter) throws ServiceError{
		try {
			return this.dao.list(filter).stream().map(SolicitudDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de solicitudes", e);
		}
	}
	
	public SolicitudDto get(long id) throws ServiceException {
		try ( val t = dao.open(true) ) {			
			return SolicitudDto.from(get(t, id));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
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
