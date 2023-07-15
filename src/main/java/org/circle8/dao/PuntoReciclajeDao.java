package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import lombok.val;
import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;

public class PuntoReciclajeDao extends Dao{

	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";
	private static final String SELECT = """
		   SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", prtr."TipoResiduoId", tr."Nombre", ciu."UsuarioId"
		     FROM "PuntoReciclaje" AS pr
		LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		    WHERE 1=1
		""";
	private static final String WHERE_TIPO = """
		    AND pr."ID" IN (
		    SELECT spr."ID"
		      FROM "PuntoReciclaje" spr
		 LEFT JOIN "PuntoReciclaje_TipoResiduo" AS sprtr ON sprtr."PuntoReciclajeId" = spr."ID"
		 LEFT JOIN "TipoResiduo" AS str on str."ID" = sprtr."TipoResiduoId"
		     WHERE str."Nombre" IN (%s)
		    )
		""";

	@Inject
	PuntoReciclajeDao(DataSource ds) {
		super(ds);
	}


	public PuntoReciclaje save(Transaction t, PuntoReciclaje punto) throws PersistenceException {
		var insertSQL = """
			INSERT INTO public."PuntoReciclaje"(
				"CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo")
				VALUES (?, ?, ?, ?, ?)""";

		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, punto.recicladorId);
			insert.setDouble(2, punto.latitud);
			insert.setDouble(3, punto.longitud);
			insert.setString(4, Dia.getDias(punto.dias));
			insert.setString(5, punto.titulo);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the PuntoReciclaje failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					punto.id = rs.getLong(1);
				else
					throw new SQLException("Creating the PuntoReciclaje failed, no ID obtained");
			}
		} catch ( SQLException e ) {
				throw new PersistenceException("error inserting PuntoReciclaje", e);
		}

		return punto;
	}

	/**
	 * Obtiene el listado de puntos de reciclaje
	 */
	public List<PuntoReciclaje> list(PuntoReciclajeFilter filter) throws PersistenceException{
		try ( var t = open(true); var select = createSelectForList(t, filter) ) {
			try ( var rs = select.executeQuery() ) {
				return getList(rs, filter);
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	/**
	 * Procesa el resultado de la consulta de list
	 * Agrupa los tipos de residuo por cada punto
	 * Valida el filtro de dias
	 */
	private List<PuntoReciclaje> getList(ResultSet rs, PuntoReciclajeFilter filter) throws SQLException{
		var mapPuntos = new HashMap<Long, PuntoReciclaje>();
		while ( rs.next() ) {
			val id = rs.getLong("ID");
			PuntoReciclaje punto = mapPuntos.get(id);
			if ( punto == null) {
				punto = new PuntoReciclaje(
					id,
					rs.getString("Titulo"),
					rs.getDouble("Latitud"),
					rs.getDouble("Longitud"),
					Dia.getDia(rs.getString("DiasAbierto")),
					new ArrayList<>(),
					rs.getLong("UsuarioId"),
					null
				);
				mapPuntos.put(id, punto);
			}
			punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}

		return mapPuntos.values().stream()
			.filter(p -> !filter.hasDias() || !Collections.disjoint(filter.dias, p.dias))
			.toList();
	}

	/**
	 */
	private PreparedStatement createSelectForList(Transaction t, PuntoReciclajeFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();

		if ( f.hasTipo() ) {
			String marks = f.tiposResiduos.stream()
					.map(tr -> "?")
					.collect(Collectors.joining(","));

			b.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tiposResiduos);
		}

		if( f.hasReciclador() ) {
			b.append("AND pr.\"CiudadanoId\" = ?\n");
			parameters.add(f.reciclador_id);
		}

		if ( f.hasArea() ) {
			b.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}
}
