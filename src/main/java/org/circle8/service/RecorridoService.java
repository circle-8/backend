package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.Transaction;
import org.circle8.dto.PuntoDto;
import org.circle8.dto.RecorridoDto;
import org.circle8.entity.Punto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Retiro;
import org.circle8.exception.ForeignKeyException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.RecorridoExpand;

import java.util.ArrayList;
import java.util.List;

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
			var r = this.dao.get(t, id, x)
				.orElseThrow(() -> new NotFoundException("No existe la solicitud"));
			sortRetiro(r.puntoInicio, r.puntos);
			return r;
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

	public RecorridoDto updateInicio(PuntoDto punto, long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.saveInicio(t, punto.toEntity(), id);
			return RecorridoDto.from(dao.get(t, id, new RecorridoExpand(new ArrayList<>())).
				orElseThrow(() -> new NotFoundException("No existe el recorrido")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar la fechaInicio del recorrido", e);
      }

   }

	public RecorridoDto updateFin(PuntoDto punto, long id) throws ServiceException {
		try ( val t = dao.open(true) ) {
			dao.saveFin(t, punto.toEntity(), id);
			return RecorridoDto.from(dao.get(t, id, new RecorridoExpand(new ArrayList<>())).
				orElseThrow(() -> new NotFoundException("No existe el recorrido")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar la fechaFin del recorrido", e);
		}
	}

	public RecorridoDto putSave(RecorridoDto dto) throws ServiceException{
		try ( val t = dao.open(true) ) {
			dao.putSave(t, dto);
			return RecorridoDto.from(dao.get(t, dto.id, new RecorridoExpand(new ArrayList<>())).
				orElseThrow(() -> new NotFoundException("No existe el recorrido")));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar el recorrido", e);
		}
	}
}
