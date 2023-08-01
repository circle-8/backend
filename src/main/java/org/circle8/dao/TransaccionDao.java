package org.circle8.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Transaccion;
import org.circle8.entity.Transporte;
import org.circle8.entity.Transportista;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

import lombok.val;

public class TransaccionDao extends Dao{

	private static final String SELECT_GET = """
			SELECT tr."ID", tr."FechaPrimerContacto", tr."FechaEfectiva", tr."PuntoReciclajeId", tr."TransporteId",
		 							 pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."CiudadanoId",
		 							 tra."FechaAcordada", tra."FechaInicio", tra."FechaFin", tra."Precio", tra."TransportistaId", tran."UsuarioId", tra."PagoConfirmado", tra."EntregaConfirmada",
		 							 re."ID" AS residuoId, re."FechaCreacion", re."FechaRetiro", re."PuntoResiduoId", re."Descripcion", re."FechaLimiteRetiro",
		 		       				 re."TipoResiduoId", re."RecorridoId", tre."Nombre" AS TipoResiduoNombre
		 					FROM public."TransaccionResiduo" AS tr
		 					LEFT JOIN Public."PuntoReciclaje" AS pr on tr."PuntoReciclajeId"=pr."ID"
		 					LEFT JOIN Public."Transporte" AS tra on tr."TransporteId"=tra."ID"
		 					LEFT JOIN Public."Transportista" AS tran on tra."TransportistaId" = tran."ID"
		 					LEFT JOIN Public."Residuo" AS re on tr."ID" = re."TransaccionId"
		 					LEFT JOIN Public."TipoResiduo" AS tre on re."TipoResiduoId" = tre."ID"
					WHERE tr."ID" = ?
		""";

	@Inject
	TransaccionDao(DataSource ds) {
		super(ds);
	}


	public Optional<Transaccion> get(long transaccionId) throws PersistenceException {
		try (var t = open(true)) {
			return get(t, transaccionId);
		}
	}

	public Optional<Transaccion> get(Transaction t, Long transaccionId) throws PersistenceException {
		try ( val ps = t.prepareStatement(SELECT_GET) ) {
			ps.setLong(1, transaccionId);

			try (val rs = ps.executeQuery()) {
				if (!rs.next()){
					return Optional.empty();
				}
				val residuos = new ArrayList<Residuo>();

				val creacionTimestamp = rs.getTimestamp("FechaPrimerContacto");
				val retiroTimestamp = rs.getTimestamp("FechaEfectiva");
				val fechaInicioTimestamp = rs.getTimestamp("FechaInicio");
				val fechaAcordadaTimestamp = rs.getTimestamp("FechaAcordada");
				val fechaFinTimestamp = rs.getTimestamp("FechaFin");

				val tr = new Transaccion(
					rs.getLong("ID"),
					creacionTimestamp != null ? creacionTimestamp.toInstant().atZone(Dates.UTC) : null,
					retiroTimestamp != null ? retiroTimestamp.toInstant().atZone(Dates.UTC) : null,
					rs.getLong("TransporteId"),
					new Transporte(rs.getLong("TransporteId"),
										fechaAcordadaTimestamp != null ? fechaAcordadaTimestamp.toInstant().atZone(Dates.UTC) : null,
										fechaInicioTimestamp != null ? fechaInicioTimestamp.toInstant().atZone(Dates.UTC) : null,
										fechaFinTimestamp != null ? fechaFinTimestamp.toInstant().atZone(Dates.UTC) : null,
										rs.getBigDecimal("Precio"),
										rs.getLong("TransportistaId"),
										new Transportista(rs.getLong("UsuarioId")),
										rs.getLong("ID"),
										rs.getBoolean("PagoConfirmado"),
										rs.getBoolean("EntregaConfirmada")),
					rs.getLong("PuntoReciclajeId"),
					new PuntoReciclaje(
						rs.getLong("ID"),
						rs.getString("Titulo"),
						rs.getDouble("Latitud"),
						rs.getDouble("Longitud"),
						Dia.getDia(rs.getString("DiasAbierto")),
						new ArrayList<>(),
						rs.getLong("CiudadanoId"),
						User.builder()
							 .id(rs.getLong("UsuarioId"))
							 .ciudadanoId(rs.getLong("CiudadanoId"))
							 .build()),
					residuos);
				do {
					val limit = rs.getTimestamp("FechaLimiteRetiro");
					val retiro = rs.getTimestamp("FechaRetiro");
					val limitDate = limit != null ? limit.toInstant().atZone(Dates.UTC) : null;
					val retiroDate = retiro != null ? retiro.toInstant().atZone(Dates.UTC) : null;

					val r = Residuo.builder()
										.id(rs.getLong("ResiduoId"))
										.ciudadanoId(rs.getLong("CiudadanoId"))
										.fechaCreacion(rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC))
										.fechaRetiro(retiroDate)
										.fechaLimiteRetiro(limitDate)
										.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
										.puntoResiduo(new PuntoResiduo(rs.getLong("PuntoResiduoId")))
										.descripcion(rs.getString("Descripcion"))
										.build();
					residuos.add(r);
				} while ( rs.next() );
				return Optional.of(tr);
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error selecting transaccion", e);
		}
	}
}
