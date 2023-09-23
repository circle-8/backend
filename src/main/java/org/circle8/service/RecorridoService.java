package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.ResiduoDao;
import org.circle8.dto.RecorridoDto;
import org.circle8.entity.Punto;
import org.circle8.entity.Retiro;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.ResiduoExpand;
import org.circle8.filter.RecorridoFilter;
import org.circle8.filter.ResiduosFilter;
import org.circle8.utils.PuntoUtils;

import java.util.List;

public class RecorridoService {

	public enum UpdateEnum {

		INICIO,
		FIN,
		RETIRO;

	}
	private final RecorridoDao dao;
	private final ResiduoDao residuoDao;

	@Inject
	public RecorridoService(RecorridoDao dao, ResiduoDao residuoDao) {
		this.dao = dao;
		this.residuoDao = residuoDao;
	}

	public RecorridoDto get(long id, RecorridoExpand x) throws ServiceException {
		try ( val t = dao.open(true) ) {
			var r = this.dao.get(t, id, x)
				.orElseThrow(() -> new NotFoundException("No existe la solicitud"));
			sortRetiro(r.puntoInicio, r.puntos);
			return RecorridoDto.from(r);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al buscar el recorrido", e);
		}
	}

	private void sortRetiro(Punto initialPoint, List<Retiro> points) {
		points.sort((a, b) -> {
			val d1 = PuntoUtils.calculateDistance(initialPoint, new Punto(a.latitud, a.longitud));
			val d2 = PuntoUtils.calculateDistance(initialPoint, new Punto(b.latitud, b.longitud));
			return Double.compare(d1, d2);
		});
	}

	public RecorridoDto save(RecorridoDto dto) throws ServiceException {
		try( var t = dao.open(true) ) {
			// TODO: Check reciclador has same zona (cuando esté el get de reciclador urbano)

			val r = dao.save(t, dto.toEntity());
			dto.id = r.id;

			return dto;
		} catch ( ForeignKeyException e ) {
			throw new ServiceException(e.getMessage());
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al guardar el recorrido", e);
		}
	}

	public void delete(long id, long zonaId) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.delete(t, id, zonaId);
		} catch ( ForeignKeyException e ) {
			throw new ServiceException(e.getMessage(), e);
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al eliminar el recorrido", e);
		}
	}


	public RecorridoDto update(RecorridoDto dto, UpdateEnum o) throws ServiceException {
		try ( val t = dao.open(true) ) {
			val recorrido = dto.toEntity();

			if ( UpdateEnum.FIN.equals(o) ) {
				val f = ResiduosFilter.builder()
					.retirado(false)
					.recorrido(dto.id)
					.build();
				val residuosNoRetirados = residuoDao.list(t, f, ResiduoExpand.EMPTY);
				if (!residuosNoRetirados.isEmpty())
					throw new ServiceException("No puede finalizar el recorrido si no ha retirado todos los residuos");
			}

			dao.update(t, recorrido, o);

			return dao.get(t, dto.id, RecorridoExpand.EMPTY)
						 .map(RecorridoDto::from)
						 .orElseThrow(() -> new NotFoundException("No existe el recorrido"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el recorrido", e);
      }
   }

	public List<RecorridoDto> list(RecorridoFilter f) throws ServiceException {
		try {
			return this.dao.list(f).stream().map(RecorridoDto::from).toList();
		} catch ( PersistenceException e ) {
			throw new ServiceError("Ha ocurrido un error al obtener los recorridos", e);
		}
	}
}
