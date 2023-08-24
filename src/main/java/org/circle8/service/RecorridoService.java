package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.RecorridoDao;
import org.circle8.dto.RecorridoDto;
import org.circle8.entity.Punto;
import org.circle8.entity.Retiro;
import org.circle8.enums.RecorridoEnum;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;
import java.util.List;

public class RecorridoService {
	private final RecorridoDao dao;

	@Inject
	public RecorridoService(RecorridoDao dao) { this.dao = dao; }

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
			val d1 = calculateDistance(initialPoint, new Punto(a.latitud, a.longitud));
			val d2 = calculateDistance(initialPoint, new Punto(b.latitud, b.longitud));
			return Double.compare(d1, d2);
		});
	}

	private double calculateDistance(Punto pointA, Punto pointB) {
		val earthRadiusKm = 6371.0;
		val lat1Rad = Math.toRadians(pointA.latitud);
		val lon1Rad = Math.toRadians(pointA.longitud);
		val lat2Rad = Math.toRadians(pointB.latitud);
		val lon2Rad = Math.toRadians(pointB.longitud);

		val dLat = lat2Rad - lat1Rad;
		val dLon = lon2Rad - lon1Rad;

		val a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
			+ Math.cos(lat1Rad) * Math.cos(lat2Rad)
			* Math.sin(dLon / 2) * Math.sin(dLon / 2);

		val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return earthRadiusKm * c;
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


	public RecorridoDto updateDate(long id, RecorridoEnum o) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.updateDate(t, id, o);
			val updatedRecorrido = dao.get(t, id, RecorridoExpand.EMPTY);
			return RecorridoDto.from(updatedRecorrido.
				orElseThrow(() -> new NotFoundException("No existe el recorrido")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el recorrido", e);
      }
   }

	public RecorridoDto putSave(RecorridoDto dto) throws ServiceException {
		try (val t = dao.open(true)) {
			val recorrido = dto.toEntity();
			recorrido.id = dto.id;
			dao.putSave(t, recorrido);
			return RecorridoDto.from(dao.get(t, dto.id, RecorridoExpand.EMPTY).orElseThrow(() -> new NotFoundException("No existe el recorrido")));
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
