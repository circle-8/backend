package org.circle8.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.circle8.dao.TransaccionDao;
import org.circle8.dao.TransporteDao;
import org.circle8.dto.PuntoResiduoDto;
import org.circle8.dto.ResiduoDto;
import org.circle8.dto.TransaccionDto;
import org.circle8.entity.Punto;
import org.circle8.entity.Residuo;
import org.circle8.entity.Transporte;
import org.circle8.exception.BadRequestException;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.exception.ServiceError;
import org.circle8.exception.ServiceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.TransaccionFilter;

import com.google.inject.Inject;

import io.jsonwebtoken.lang.Collections;
import lombok.val;

public class TransaccionService {

	private final TransaccionDao dao;
	private final TransporteDao transporteDao;
	private final Configuration config;

	@Inject
	public TransaccionService(TransaccionDao dao,TransporteDao transporteDao,Configuration config) {
		this.dao = dao;
		this.transporteDao = transporteDao;
		this.config = config;
	}

	public TransaccionDto get(Long transaccionId, TransaccionExpand expand) throws ServiceException {
		try {
			return this.dao.get(transaccionId, expand).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener la transaccion", e);
		}
	}

	public List<TransaccionDto> list(TransaccionFilter f, TransaccionExpand x) throws ServiceException {
		try {
			return this.dao.list(f, x).stream().map(TransaccionDto::from).toList();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al obtener las transacciones", e);
		}
	}

	public TransaccionDto save(TransaccionDto dto) throws ServiceError, NotFoundException {
		var entity = dto.toEntity();
		try (var t = dao.open()) {
			entity = dao.save(t, entity);
			dto.id = entity.id;
			for (Residuo r : entity.residuos) {
				dao.saveResiduo(t, r.id, dto.id);
			}
			t.commit();
			return this.dao.get(t, dto.id, new TransaccionExpand(false,false,true)).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion que se buscaba actualizar"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al guardar la transaccion", e);
		}
	}

	public TransaccionDto put(Long id, Long residuoId) throws ServiceException {
		try (var t = dao.open()) {
			TransaccionDto dto = dao.get(t, id, new TransaccionExpand(new ArrayList<>())).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion"));
			dao.saveResiduo(t, residuoId, id);
			t.commit();
			return this.dao.get(t, dto.id, new TransaccionExpand(false,false,true)).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al tratar de agregar un residuo a la transaccion", e);
		}
	}

	public TransaccionDto put(TransaccionDto dto) throws ServiceException {
		try (var t = dao.open(true)) {
			dao.put(t, dto.toEntity());
			return dao.get(dto.id).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion"));
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al tratar de agregar un residuo a la transaccion", e);
		}
	}

	public void delete(Long transaccionId) throws ServiceError, NotFoundException {
		try (var t = dao.open()) {
			dao.removeResiduos(t, transaccionId);
			dao.delete(t, transaccionId);
			t.commit();
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al eliminar la transaccion", e);
		}
	}
	public void setTransporte(Long id, Long transporteId) throws ServiceError, NotFoundException, BadRequestException {
		try (var t = dao.open(true)) {
			dao.setTransporte(t, id, transporteId);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al intentar añadir el transporte de la transaccion", e);
      }
   }

	public void removeResiduo(Long transaccionId, Long residuoId) throws ServiceError, NotFoundException {
		try (var t = dao.open(true)) {
			dao.removeResiduo(t, transaccionId, residuoId);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al intentar remover el residuo de la transaccion", e);
		}
	}

	public void removeTransporte(Long id, Long transporteId) throws NotFoundException, ServiceError {
		try (var t = dao.open(true)) {
			dao.removeTransporte(t, id, transporteId);
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al intentar remover el transporte de la transaccion", e);
		}
	}
	
	public TransaccionDto createTransporte(Long id) throws NotFoundException, ServiceError, BadRequestException {
		try (var t = dao.open()) {			
			var transaccion = dao.get(id, new TransaccionExpand(false,false,true)).map(TransaccionDto::from).orElseThrow(() -> new NotFoundException("No existe la transaccion"));
			if(transaccion.transporteId != null && transaccion.transporteId != 0L)
				throw new BadRequestException("La transaccion ya posee un transporte");
			var transporte = Transporte.builder().precioSugerido(getPrecioSugerido(transaccion)).build();
			this.transporteDao.save(t, transporte);
			transaccion.transporteId = transporte.id;
			dao.setTransporte(t, id, transporte.id);
//			t.commit();
			return transaccion;
		} catch (PersistenceException e) {
			throw new ServiceError("Ha ocurrido un error al intentar remover el transporte de la transaccion", e);
		}
	}
	
	private BigDecimal getPrecioSugerido(TransaccionDto transaccion) {
		var precio = BigDecimal.ZERO;
		if(!Collections.isEmpty(transaccion.residuos)) {
			var cantidadPuntos = new BigDecimal(transaccion.residuos.size());
			var valorPuntos = new BigDecimal(config.getLong("VALOR_POR_PUNTOS"));
			var distancia = getDistancia(transaccion.residuos);
			var valoDistancia = new BigDecimal(config.getLong("VALOR_POR_KM"));
			var totalPuntos = cantidadPuntos.multiply(valorPuntos);
			var totalDistancia = distancia.multiply(valoDistancia);			
			precio = totalPuntos.add(totalDistancia);
		}		
		return precio;
	}
	
	private BigDecimal getDistancia(List<ResiduoDto> residuos) {
		var distancia = BigDecimal.ZERO;
		if(!residuos.isEmpty()) {
			sortResiduos(residuos.get(0).puntoResiduo, residuos);			
		}
		
		return distancia;
	}
	
	private void sortResiduos(PuntoResiduoDto puntoResiduo, List<ResiduoDto> points) {
		var puntoInicial = new Punto(puntoResiduo.latitud, puntoResiduo.longitud);
		points.sort((a, b) -> {
			val d1 = calculateDistance(puntoInicial, new Punto(a.puntoResiduo.latitud, a.puntoResiduo.longitud));
			val d2 = calculateDistance(puntoInicial, new Punto(b.puntoResiduo.latitud, b.puntoResiduo.longitud));
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

}
