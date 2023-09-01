package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Transporte;
import org.circle8.entity.Transportista;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.TransporteExpand;
import org.circle8.utils.Dates;
import org.circle8.utils.PuntoUtils;

import com.google.inject.Inject;

import lombok.val;

public class TransporteDao extends Dao {
	
	private static final String SELECT_FMT = """
			SELECT
			      %s
			  FROM "Transporte" AS t
			    %s
			 WHERE 1 = 1
			""";
	
	private static final String SELECT_SIMPLE = """
			t."ID", t."Precio", t."FechaAcordada", t."FechaInicio", t."FechaFin", t."PagoConfirmado", t."EntregaConfirmada", t."TransportistaId", t."PrecioSugerido"
			""";
	
	private static final String SELECT_X_TRANSPORTISTA = """
			, transp."UsuarioId", transp."Polyline"
			""";
	
	private static final String SELECT_X_TRANSACCION = """
			, trans."ID" AS transaccionID
			""";
	
	private static final String JOIN_X_TRANSPORTISTA = """
			LEFT JOIN "Transportista" AS transp on transp."ID" = t."TransportistaId"
			""";
	
	private static final String JOIN_X_TRANSACCION = """
			LEFT JOIN "TransaccionResiduo" AS trans on trans."TransporteId" = t."ID"
			""";
	
	private static final String WHERE_ID = """
			AND t."ID" = ?
			""";


	@Inject
	TransporteDao(DataSource ds) {
		super(ds);
	}

	public Optional<Transporte> get(Transaction t, long id, TransporteExpand x) throws PersistenceException {
		try ( val select = createSelect(t, id, x) ) {
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(buildTransporte(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting transporte", e);
		}
	}
	
	
	private PreparedStatement createSelect(Transaction t, Long id, TransporteExpand x)
			throws PersistenceException, SQLException {
		
		var selectFields = SELECT_SIMPLE;
		var joinFields = "";

		if (x.transportista) {
			selectFields += SELECT_X_TRANSPORTISTA;
			joinFields += JOIN_X_TRANSPORTISTA;
		}
		
		if(x.transaccion) {
			selectFields += SELECT_X_TRANSACCION;
			joinFields += JOIN_X_TRANSACCION;
		}

		var sql = String.format(SELECT_FMT, selectFields, joinFields);
		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		appendCondition(id, WHERE_ID, b, parameters);
		
		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}
	
	private Transporte buildTransporte(ResultSet rs, TransporteExpand x) throws SQLException {
		val t = new Transporte();
		t.id = rs.getLong("ID");
		if(rs.getTimestamp("FechaAcordada") != null)
			t.fechaAcordada = rs.getTimestamp("FechaAcordada").toInstant().atZone(Dates.UTC);
		if(rs.getTimestamp("FechaInicio") != null)
			t.fechaInicio = rs.getTimestamp("FechaInicio").toInstant().atZone(Dates.UTC);
		if(rs.getTimestamp("FechaFin") != null)
			t.fechaFin = rs.getTimestamp("FechaFin").toInstant().atZone(Dates.UTC);
		t.precioAcordado = rs.getBigDecimal("Precio");
		t.transportistaId = rs.getLong("TransportistaId") != 0 ? rs.getLong("TransportistaId"): null;
		t.transportista = buildTransportista(rs, x.transportista);
		if(x.transaccion && rs.getLong("TransaccionId") != 0)
			t.transaccionId = rs.getLong("TransaccionId");
		t.pagoConfirmado = rs.getBoolean("PagoConfirmado");
		t.entregaConfirmada = rs.getBoolean("EntregaConfirmada");
		t.precioSugerido = rs.getBigDecimal("PrecioSugerido");

		return t;
	}
	
	private Transportista buildTransportista(ResultSet rs, boolean expand) throws SQLException {
		if ( !expand || rs.getLong("TransportistaId") == 0)
			return null;
		
		return Transportista.builder()
				.id(rs.getLong("TransportistaId"))
				.usuarioId(rs.getLong("UsuarioId"))
				.polyline(PuntoUtils.getPolyline(rs.getString("Polyline")))
				.build();

	}

}
