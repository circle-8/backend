package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.ResiduoDao;
import org.circle8.dao.SolicitudDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.ZonaDao;
import org.circle8.dto.ResiduoDto;
import org.circle8.entity.EstadoSolicitud;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Residuo;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.ResiduoExpand;
import org.circle8.expand.SolicitudExpand;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ResiduosFilter;
import org.circle8.filter.SolicitudFilter;
import org.circle8.filter.ZonaFilter;
import org.circle8.utils.Dates;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ResiduoService {
	private final ResiduoDao dao;
	private final SolicitudDao solicitudDao;
	private final ZonaDao zonaDao;

	@Inject
	public ResiduoService(ResiduoDao dao, SolicitudDao solicitudDao, ZonaDao zonaDao) {
		this.dao = dao;
		this.solicitudDao = solicitudDao;
		this.zonaDao = zonaDao;
	}

	public ResiduoDto save(ResiduoDto dto) throws ServiceException {
		try( var t = dao.open(true)) {
			var residuo = dao.save(t, dto.toEntity());
			dto.id = residuo.id;
			return dto;
		} catch ( ForeignKeyException e ) {
			throw new ServiceException(e.getMessage());
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el residuo", e);
		}
	}

	public ResiduoDto fulfill(long residuoId) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = find(t, residuoId, ResiduoExpand.EMPTY);

			if ( r.fechaRetiro != null )
				throw new ServiceException("El residuo ya ha sido retirado previamente");

			r.fechaRetiro = ZonedDateTime.now(Dates.UTC);
			dao.update(t, r);

			return ResiduoDto.from(r);
		} catch ( ForeignKeyException e ) {
			throw new NotFoundException(e.getMessage());
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el residuo", e);
		}
	}

	public ResiduoDto get(long id, ResiduoExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			return this.get(t, id, x)
				.map(ResiduoDto::from)
				.orElseThrow(() -> new NotFoundException("No existe el residuo"));
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el residuo", e);
		}
	}

	Optional<Residuo> get(Transaction t, long id, ResiduoExpand x) throws PersistenceException {
		return dao.get(t, id, x);
	}

	Residuo find(Transaction t, long id, ResiduoExpand x) throws PersistenceException, NotFoundException {
		return get(t, id, x).orElseThrow(() -> new NotFoundException("El residuo no existe"));
	}

	public List<ResiduoDto> list(ResiduosFilter f, ResiduoExpand x) throws ServiceException {
		try {
			return this.dao
				.list(f, x)
				.stream()
				.map(ResiduoDto::from)
				.toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener los residuos", e);
		}
	}

	public ResiduoDto addToRecorrido(long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = find(t, id, ResiduoExpand.EMPTY);
			if ( r.fechaRetiro != null )
				throw new ServiceException("El residuo ya ha sido retirado previamente");
			if ( r.transaccion != null && r.transaccion.id != 0 )
				throw new ServiceException("El residuo ya es parte de una transacción");
			if ( r.recorrido != null && r.recorrido.id != 0 )
				throw new ServiceException("El residuo ya es parte de un recorrido");

			if ( residuoHasSolicitudes(t, id) )
				throw new ServiceException("El residuo tiene solicitudes pendientes");

			var f = ZonaFilter.builder().puntoResiduoId(r.puntoResiduo.id).build();
			var x = ZonaExpand.builder().recorridos(true).build();
			var zonaWithTipo = zonaDao.list(t, f, x)
				.stream()
				.filter(z -> z.tipoResiduo.contains(r.tipoResiduo))
				.findAny()
				.orElseThrow(() -> new ServiceException("El punto de residuo no tiene asociada una zona de reciclaje para este tipo de residuo"));

			/* la zona debe tener un proximo recorrido activo */
			r.recorrido = zonaWithTipo.recorridos
				.stream()
				.filter(rec -> rec.fechaInicio == null) // Todavia no ha comenzado
				.min(Comparator.comparing(Recorrido::getFechaRetiro))
				.orElseThrow(() -> new ServiceException("La zona no posee un recorrido pendiente"));

			dao.update(t, r);
			return ResiduoDto.from(r);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al agregar residuo a recorrido", e);
		}
	}

	private boolean residuoHasSolicitudes(Transaction t, long id) throws PersistenceException {
		var solicitudes = solicitudDao.list(
			t,
			SolicitudFilter.builder().residuoId(id).notEstados(List.of(EstadoSolicitud.CANCELADA)).build(),
			SolicitudExpand.EMPTY
		);
		return !solicitudes.isEmpty();
	}

	public ResiduoDto deleteFromRecorrido(long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = find(t, id, ResiduoExpand.EMPTY);
			if ( r.recorrido == null || r.recorrido.id == 0 )
				throw new ServiceException("El residuo no es parte de un recorrido");
			if ( r.fechaRetiro != null )
				throw new ServiceException("El residuo ya ha sido retirado previamente");

			r.recorrido = null;
			dao.update(t, r);

			return ResiduoDto.from(r);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al borrar residuo del recorrido", e);
		}
	}

	public void delete(Long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.delete(t, id);
		} catch ( ForeignKeyException e ) {
			throw new ServiceException(e.getMessage(), e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al eliminar el residuo", e);
		}
	}

	// TODO: los updates no deberian hacerse con el DTO. Crearia un ResiduoUpdate
	public ResiduoDto update(ResiduoDto dto, long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = find(t, id, ResiduoExpand.EMPTY);

			if ( r.transaccion != null && r.transaccion.id != 0L )
				throw new ServiceException("El residuo ya pertenece a una transaccion");
			if ( r.recorrido != null && r.recorrido.id != 0L )
				throw new ServiceException("El residuo ya pertenece a un recorrido");
			if ( residuoHasSolicitudes(t, id) )
				throw new ServiceException("El residuo tiene solicitudes pendientes");

			r.tipoResiduo = dto.tipoResiduo.toEntity();
			r.fechaLimiteRetiro = dto.fechaLimiteRetiro;
			r.descripcion = dto.descripcion;
			r.base64 = dto.base64;

			dao.update(t, r);

			return ResiduoDto.from(r);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al borrar residuo del recorrido", e);
		}
	}
}
