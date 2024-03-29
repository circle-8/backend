package org.circle8.service;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dao.PuntoResiduoDao;
import org.circle8.dao.RecicladorUrbanoDao;
import org.circle8.dao.RecorridoDao;
import org.circle8.dao.ResiduoDao;
import org.circle8.dao.Transaction;
import org.circle8.dao.ZonaDao;
import org.circle8.dto.TipoResiduoDto;
import org.circle8.dto.ZonaDto;
import org.circle8.entity.Punto;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Zona;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.expand.RecorridoExpand;
import org.circle8.expand.ResiduoExpand;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ResiduosFilter;
import org.circle8.filter.ZonaFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZonaService {

	private final ZonaDao dao;
	private final PuntoResiduoDao puntoResiduoDao;
	private final RecicladorUrbanoDao recicladorUrbanoDao;
	private final RecorridoDao recorridoDao;
	private final ResiduoDao residuoDao;

	@Inject
	public ZonaService(ZonaDao dao, PuntoResiduoDao puntoResiduoDao,
			RecicladorUrbanoDao recicladorUrbanoDao,
			RecorridoDao recorridoDao, ResiduoDao residuoDao) {
		this.dao = dao;
		this.puntoResiduoDao = puntoResiduoDao;
		this.recicladorUrbanoDao = recicladorUrbanoDao;
		this.recorridoDao = recorridoDao;
		this.residuoDao = residuoDao;
	}

	public ZonaDto save(Long organizacionId, ZonaDto dto) throws ServiceException {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			entity = dao.save(t, organizacionId, entity);
			for (TipoResiduo tr : entity.tipoResiduo) {
				dao.saveTipos(t, entity.id, tr.id);
			}
			t.commit();
			val f = ZonaFilter.builder().id(entity.id).build();
			val x = new ZonaExpand(true, false, false);
			return get(f, x);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar la zona", e);
		}
	}

	public ZonaDto put(Long organizacionId, Long zonaId, ZonaDto dto) throws ServiceException {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			this.dao.deleteTipos(t, zonaId);
			this.dao.update(t, zonaId, entity);
			val puntosActuales = dao.getPuntosResiduo(t, zonaId);

			for (TipoResiduoDto tr : dto.tipoResiduo) {
				dao.saveTipos(t, zonaId, tr.id);
			}

			for (PuntoResiduo punto : puntosActuales) {
				if (!acceptPunto(entity.polyline, punto))
					this.dao.excludePuntoResiduo(t, punto.id, zonaId);
				// TODO: notificar al cliente que lo sacaron de la zona, nice to have
			}

			t.commit();
			val f = ZonaFilter.builder().id(zonaId).build();
			val x = new ZonaExpand(true, false, true);
			return get(f, x);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al actualizar la zona", e);
		}
	}

	public ZonaDto get(ZonaFilter f, ZonaExpand x) throws ServiceException {
		try (val t = dao.open(true)) {
			return get(t, f, x).map(ZonaDto::from).orElseThrow(() -> new NotFoundException("No existe la zona"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al buscar la zona", e);
		}
	}

	private Optional<Zona> get(Transaction t, ZonaFilter f, ZonaExpand x) throws PersistenceException {
		return this.dao.get(t, f, x);
	}

	public List<ZonaDto> list(ZonaFilter f, ZonaExpand x) throws ServiceError {
		try {
			return this.dao.list(f, x).stream().map(ZonaDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener el listado de zonas", e);
		}
	}

	public ZonaDto includePuntoResiduo(Long puntoResiduoId, Long zonaId) throws ServiceException {
		try (val t = dao.open()) {
			val f = new ZonaFilter(zonaId);
			val x = new ZonaExpand(false, false, true);

			val zona = get(t, f, x).orElseThrow(() -> new NotFoundException("No existe la zona"));

			val punto = this.puntoResiduoDao.get(null, puntoResiduoId, PuntoResiduoExpand.EMPTY)
					.orElseThrow(() -> new NotFoundException("No existe el punto de residuo"));

			if (!acceptPunto(zona.polyline, punto))
				throw new ServiceException("El punto no se encuentra dentro de la zona.");

			this.dao.includePuntoResiduo(t, puntoResiduoId, zonaId);

			if (zona.puntosResiduos == null)
				zona.puntosResiduos = new ArrayList<>();

			t.commit();

			zona.puntosResiduos.add(punto);
			return ZonaDto.from(zona);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar el punto de residuo en la zona", e);
		}
	}

	public ZonaDto excludePuntoResiduo(Long puntoResiduoId, Long zonaId) throws ServiceException {
		try (val t = dao.open()) {
			var rf = ResiduosFilter.builder()
					.retirado(false)
					.puntosResiduo(List.of(puntoResiduoId))
					.build();
			var residuos = residuoDao.list(rf, ResiduoExpand.EMPTY);
			for(Residuo r : residuos) {
				if(r.recorrido != null && r.recorrido.id != 0) {
					var rec = recorridoDao.get(t, r.recorrido.id, RecorridoExpand.EMPTY);
					if(rec.isPresent() && zonaId.equals(rec.get().zonaId))
						throw new ServiceException("Hay residuos asociados al circuito de reciclaje");
				}
			}			
			
			this.dao.excludePuntoResiduo(t, puntoResiduoId, zonaId);

			val zf = new ZonaFilter(zonaId);
			val zx = new ZonaExpand(false, false, true);
			val zona = get(t, zf, zx).orElseThrow(() -> new NotFoundException("No existe la zona"));

			t.commit();

			return ZonaDto.from(zona);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al eliminar el punto de residuo de la zona", e);
		}
	}

	public void delete(Long organizacionId, Long zonaId) throws ServiceException {
		try (val t = dao.open(false)) {
			this.dao.deleteTipos(t, zonaId);
			this.dao.deletePuntos(t, zonaId);
			this.recicladorUrbanoDao.desasociarZona(t, null, zonaId);
			this.recorridoDao.desasociarZona(t, zonaId);
			this.dao.delete(t, organizacionId, zonaId);
			t.commit();
		}catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al eliminar la zona", e);
		}
	}

	/**
	 * Se utiliza el algoritmo de Algoritmo de Ray Casting Si el número de
	 * intersecciones es impar, el punto está dentro del polígono Se usa el mismo
	 * algoritmo en el front
	 */
	private boolean acceptPunto(List<Punto> polyline, PuntoResiduo punto) {
		int numIntersecciones = 0;
		val n = polyline.size();
		for (int i = 0; i < n; i++) {
			val punto1 = polyline.get(i);
			val punto2 = polyline.get((i + 1) % n);

			if (punto.latitud > Math.min(punto1.latitud, punto2.latitud)
					&& punto.latitud <= Math.max(punto1.latitud, punto2.latitud)
					&& punto.longitud <= Math.max(punto1.longitud, punto2.longitud)
					&& punto1.latitud != punto2.latitud) {

				double xInterseccion = (punto.latitud - punto1.latitud) * (punto2.longitud - punto1.longitud)
						/ (punto2.latitud - punto1.latitud) + punto1.longitud;
				if (punto1.longitud == punto2.longitud || punto.longitud <= xInterseccion) {
					numIntersecciones++;
				}
			}
		}

		return numIntersecciones % 2 != 0;
	}

}
