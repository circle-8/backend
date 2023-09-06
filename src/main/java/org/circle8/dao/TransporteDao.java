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
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.TransporteExpand;
import org.circle8.filter.TransporteFilter;
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
	
	private static final String WHERE_TRANSACCION_ID = """
			AND trans."ID" = ?
			""";
	
	private static final String WHERE_ID = """
			AND t."ID" = ?
			""";
	
	private static final String WHERE_TRANSPORTISTA_ID = """
			AND t."TransportistaId" = ?
			""";
	
	private static final String WHERE_FECHA = """
			AND t."FechaAcordada" = ?
			""";
	
	private static final String WHERE_ENTREGA_CONFIRMADA = """
			AND t."EntregaConfirmada" = true
			""";
	
	private static final String WHERE_ENTREGA_NO_CONFIRMADA = """
			AND t."EntregaConfirmada" = false
			""";
	
	private static final String WHERE_PAGO_CONFIRMADO = """
			AND t."PagoConfirmado" = true
			""";
	
	private static final String WHERE_PAGO_NO_CONFIRMADO = """
			AND t."PagoConfirmado" = false
			""";

	private static final String WHERE_TRANSPORTISTA_NULL = """
			AND t."TransportistaId" IS NULL
			""";
	
	private static final String UPDATE_PAGO = """
			UPDATE "Transporte"
			SET "PagoConfirmado"=true
			WHERE "ID"=?;
			""";
	
	private static final String UPDATE_ENTREGA = """
			UPDATE "Transporte"
			SET "EntregaConfirmada"=true
			WHERE "ID"=?;
			""";

	@Inject
	TransporteDao(DataSource ds) {
		super(ds);
	}

	public Optional<Transporte> get(Transaction t, TransporteFilter f, TransporteExpand x) throws PersistenceException {
		try ( val select = createSelect(t, f, x) ) {
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(buildTransporte(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting transporte", e);
		}
	}
	
	public List<Transporte> list(TransporteFilter f, TransporteExpand x) throws PersistenceException {
		try ( var t = open(true) ) {
			return list(t, f, x);
		}
	}
	
	public List<Transporte> list(Transaction t, TransporteFilter f, TransporteExpand x) throws PersistenceException {
		try (
			var select = createSelect(t, f, x);
			var rs = select.executeQuery()
		) {
			val l = new ArrayList<Transporte>();
			while ( rs.next() ) {
				l.add(buildTransporte(rs, x));
			}

			return l;
		} catch (SQLException e) {
			throw new PersistenceException("error getting transportes", e);
		}
	}
	
	public void updatePago(Transaction t,Long id) throws NotFoundException, PersistenceException {
		try (var update = t.prepareStatement(UPDATE_PAGO)) {
			update.setLong(1, id);
			int insertions = update.executeUpdate();
			if (insertions == 0) {
				throw new NotFoundException("No existe el transporte a actualizar.");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating transporte", e);
		}
	}
	
	public void updateEntrega(Transaction t,Long id) throws NotFoundException, PersistenceException {
		try (var update = t.prepareStatement(UPDATE_ENTREGA)) {
			update.setLong(1, id);
			int insertions = update.executeUpdate();
			if (insertions == 0) {
				throw new NotFoundException("No existe el transporte a actualizar.");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating transporte", e);
		}
	}	
	
	private PreparedStatement createSelect(Transaction t, TransporteFilter f, TransporteExpand x)
			throws PersistenceException, SQLException {
		
		var selectFields = SELECT_SIMPLE;
		var joinFields = "";

		if (x.transportista) {
			selectFields += SELECT_X_TRANSPORTISTA;
			joinFields += JOIN_X_TRANSPORTISTA;
		}
		
		if(x.transaccion || f.transaccionId != null) {
			selectFields += SELECT_X_TRANSACCION;
			joinFields += JOIN_X_TRANSACCION;
		}

		var b = new StringBuilder(String.format(SELECT_FMT, selectFields, joinFields));
		List<Object> parameters = new ArrayList<>();
		
		appendCondition(f.id, WHERE_ID, b, parameters);
		appendCondition(f.transportistaId, WHERE_TRANSPORTISTA_ID, b, parameters);
		appendCondition(f.fechaRetiro, WHERE_FECHA, b, parameters);
		appendCondition(f.transaccionId, WHERE_TRANSACCION_ID, b, parameters);
		
		if(f.entregaConfirmada != null)
			b.append(f.entregaConfirmada ? WHERE_ENTREGA_CONFIRMADA : WHERE_ENTREGA_NO_CONFIRMADA);
		
		if(f.pagoConfirmado != null)
			b.append(f.pagoConfirmado ? WHERE_PAGO_CONFIRMADO : WHERE_PAGO_NO_CONFIRMADO);
		
		if(f.soloSinTransportista != null && f.soloSinTransportista)
			b.append(WHERE_TRANSPORTISTA_NULL);	
		
		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}
	
	private Transporte buildTransporte(ResultSet rs, TransporteExpand x) throws SQLException {
		val t = new Transporte();
		t.id = rs.getLong("ID");
		if(rs.getDate("FechaAcordada") != null)
			t.fechaAcordada = rs.getDate("FechaAcordada").toLocalDate();
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
