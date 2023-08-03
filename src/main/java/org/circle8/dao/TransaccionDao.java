package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.TransaccionFilter;
import org.circle8.utils.Dates;

import com.google.inject.Inject;

import lombok.val;

public class TransaccionDao extends Dao{

	private static final String WHERE_ID = """
		AND tr."ID" = ?
		""";

	private static final String SELECT_FMT = """
		SELECT
		       %s
		  FROM public."TransaccionResiduo" AS tr
		    %s
		 WHERE 1 = 1
		""";

	private static final String SELECT_SIMPLE = """
		tr."ID", tr."FechaPrimerContacto", tr."FechaEfectiva", tr."PuntoReciclajeId", tr."TransporteId"
		""";

	private static final String SELECT_RESIDUOS = """
		, re."ID" AS residuoId, re."FechaCreacion", re."FechaRetiro", re."PuntoResiduoId", re."Descripcion", re."FechaLimiteRetiro",
		re."TipoResiduoId", re."RecorridoId", tre."Nombre" AS TipoResiduoNombre,
		pr."CiudadanoId"
		""";

	private static final String SELECT_TRANSPORTE = """
		, tra."FechaAcordada", tra."FechaInicio", tra."FechaFin", tra."Precio", tra."TransportistaId", tra."PagoConfirmado", tra."EntregaConfirmada"
		""";

	private static final String SELECT_PUNTO_RECICLAJE = """
		, pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."CiudadanoId"
		""";

	private static final String JOIN_PUNTO_RECICLAJE = """
		JOIN Public."PuntoReciclaje" AS pr on tr."PuntoReciclajeId"=pr."ID"
		""";

	private static final String JOIN_RESIDUOS = """
		JOIN Public."PuntoReciclaje" AS pr on tr."PuntoReciclajeId"=pr."ID"
		JOIN Public."Residuo" AS re on tr."ID" = re."TransaccionId"
		JOIN Public."TipoResiduo" AS tre on re."TipoResiduoId" = tre."ID"
		""";

	private static final String JOIN_TRANSPORTE = """
		JOIN Public."Transporte" AS tra on tr."TransporteId"=tra."ID"
		""";

	@Inject
	TransaccionDao(DataSource ds) {
		super(ds);
	}


	public Optional<Transaccion> get(long transaccionId, TransaccionExpand expand) throws PersistenceException {
		try (var t = open(true)) {
			return get(t, transaccionId, expand);
		}
	}

	public Optional<Transaccion> get(Transaction t, Long id, TransaccionExpand expand) throws PersistenceException {
		val f = TransaccionFilter.builder().id(id).build();
		try ( val ps = createSelect(t, f, expand) ) {
			ps.setLong(1, id);
			String req = ps.toString();
			try (val rs = ps.executeQuery()) {
				if (!rs.next()) {
					return Optional.empty();
				}
				return Optional.of(buildTransaccion(rs, expand));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error selecting transaccion", e);
		}
	}

	private Transaccion buildTransaccion(ResultSet rs, TransaccionExpand expand) throws SQLException {

		val transporte = buildTransporte(rs, expand.transporte);
		val puntoReciclaje = buildPuntoReciclaje(rs, expand.puntoReciclaje);
		List<Residuo> residuos = new ArrayList<>();

		val creacionTimestamp = rs.getTimestamp("FechaPrimerContacto");
		val retiroTimestamp = rs.getTimestamp("FechaEfectiva");

		val tr = new Transaccion(
			rs.getLong("ID"),
			creacionTimestamp != null ? creacionTimestamp.toInstant().atZone(Dates.UTC) : null,
			retiroTimestamp != null ? retiroTimestamp.toInstant().atZone(Dates.UTC) : null,
			rs.getLong("TransporteId"),
			transporte,
			rs.getLong("PuntoReciclajeId"),
			puntoReciclaje,
			residuos);

		buildResiduos(rs, expand.residuos, residuos);
		return tr;
	}

	private PuntoReciclaje buildPuntoReciclaje(ResultSet rs, boolean expand) throws SQLException {
		if(!expand) return null;

		return new PuntoReciclaje(
			rs.getLong("PuntoReciclajeId"),
			rs.getString("Titulo"),
			rs.getDouble("Latitud"),
			rs.getDouble("Longitud"),
			Dia.getDia(rs.getString("DiasAbierto")),
			new ArrayList<>(),
			rs.getLong("CiudadanoId"),
			null);
	}

	private Transporte buildTransporte(ResultSet rs, boolean expand) throws SQLException {
		if(!expand) return null;

		val fechaInicioTimestamp = rs.getTimestamp("FechaInicio");
		val fechaAcordadaTimestamp = rs.getTimestamp("FechaAcordada");
		val fechaFinTimestamp = rs.getTimestamp("FechaFin");

		return new Transporte(rs.getLong("TransporteId"),
			fechaAcordadaTimestamp != null ? fechaAcordadaTimestamp.toInstant().atZone(Dates.UTC) : null,
			fechaInicioTimestamp != null ? fechaInicioTimestamp.toInstant().atZone(Dates.UTC) : null,
			fechaFinTimestamp != null ? fechaFinTimestamp.toInstant().atZone(Dates.UTC) : null,
			rs.getBigDecimal("Precio"),
			rs.getLong("TransportistaId"),
			null,
			rs.getLong("ID"),
			rs.getBoolean("PagoConfirmado"),
			rs.getBoolean("EntregaConfirmada"));
	}

	private void buildResiduos(ResultSet rs, boolean expand, List<Residuo> residuos) throws SQLException {
		if(!expand) return;
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
	}

	private PreparedStatement createSelect(
		Transaction t,
		TransaccionFilter f,
		TransaccionExpand exp
	) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		if ( exp.residuos ) selectFields += SELECT_RESIDUOS;
		if ( exp.transporte ) selectFields += SELECT_TRANSPORTE;
		if ( exp.puntoReciclaje ) selectFields += SELECT_PUNTO_RECICLAJE;

		var joinFields = "";
		if ( exp.residuos ) joinFields += JOIN_RESIDUOS;
		else if ( exp.puntoReciclaje ) joinFields += JOIN_PUNTO_RECICLAJE;
		if ( exp.transporte ) joinFields += JOIN_TRANSPORTE;

		var sql = String.format(SELECT_FMT, selectFields, joinFields);
		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		if ( f.id != null ) {
			b.append(WHERE_ID);
			parameters.add(f.id);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

}
