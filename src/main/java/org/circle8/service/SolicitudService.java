package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.PuntoReciclajeDao;
import org.circle8.dao.ResiduoDao;
import org.circle8.dao.SolicitudDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.SolicitudDto;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.Solicitud;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.ResiduoExpand;
import org.circle8.expand.SolicitudExpand;
import org.circle8.filter.SolicitudFilter;

import java.util.List;
import java.util.Optional;

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

			val residuo = residuoDao.get(t, residuoId, ResiduoExpand.EMPTY)
				.orElseThrow(() -> new NotFoundException("No existe el residuo"));

			if ( residuo.fechaRetiro != null )
				throw new ServiceException("El residuo ya se ha retirado");

			val punto = puntoDao.get(t, puntoReciclajeId)
				.orElseThrow(() -> new NotFoundException("No existe el punto de reciclaje"));

			/* Validar que el punto de reciclaje acepte el tipo de residuo */
			val acceptsTipo = punto.tipoResiduo.stream().anyMatch(tr -> tr.id == residuo.tipoResiduo.id);
			if ( !acceptsTipo )
				throw new ServiceException("El Punto de Reciclaje no admite este tipo de residuo");

			if ( punto.reciclador.ciudadanoId.equals(residuo.ciudadano.id) )
				throw new ServiceException("El residuo no puede pertenecer al dueño del punto");

			val id = dao.save(t, residuoId, puntoReciclajeId, tipo);

			return SolicitudDto.from(get(t, id, SolicitudExpand.EMPTY));
		} catch ( DuplicatedEntry e ) {
			throw new ServiceException("Solicitud ya existente", e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar la solicitud", e);
		}
	}

	public List<SolicitudDto> list(SolicitudFilter f, SolicitudExpand x) throws ServiceError{
		try {
			return this.dao.list(f, x).stream().map(SolicitudDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de solicitudes", e);
		}
	}

	public SolicitudDto get(long id, SolicitudExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return SolicitudDto.from(get(t, id, x));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}

	private Solicitud get(Transaction t, long id, SolicitudExpand x) throws ServiceException {
		try {
			return this.dao.get(t, id, x)
				.orElseThrow(() -> new NotFoundException("No existe la solicitud"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar la solicitud", e);
		}
	}

	public SolicitudDto put(Long id, Long ciudadanoID, EstadoSolicitud estado) throws ServiceException {
		try( var t = dao.open() ) {			
			if ( EstadoSolicitud.APROBADA.equals(estado) ) {
				dao.aprobar(t, id, estado);
				Optional<Solicitud> solicitudOP = this.dao.get(t, id, new SolicitudExpand(false, true, false, false));						
				if(solicitudOP.isPresent() && solicitudOP.get().residuo != null) {
					Solicitud solicitud = solicitudOP.get();
					val f = SolicitudFilter.builder().residuoId(solicitud.residuo.id).build();
					val solicitudes = list(f, SolicitudExpand.EMPTY);
					for(SolicitudDto s : solicitudes) {
						if(s.id != id) {
							dao.cancelar(t, s.id, solicitud.solicitado.id, EstadoSolicitud.CANCELADA);
						}
					}
				}
			}else
				dao.cancelar(t, id, ciudadanoID, estado);
			
			t.commit();
			return SolicitudDto.from(get(t, id, SolicitudExpand.EMPTY));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al cambiar de estado la solicitud", e);
		}
	}
}
