package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import lombok.val;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajeRequest;
import org.circle8.controller.response.DiaResponse;
import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoReciclajeFilter;

import com.google.inject.Inject;

public class PuntoReciclajeDao extends Dao {

	private static final String SELECT_TIPOS = """
		   SELECT tr."ID", tr."Nombre"
		     FROM public."TipoResiduo" AS tr
		    WHERE 1=1
		""";

	private static final String WHERE_NOMBRE_TIPOS = """
			AND tr."Nombre" IN (%s)
		""";

	private static final String INSERT_SQL = """
		INSERT INTO public."PuntoReciclaje"("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo")
		VALUES (?, ?, ?, ?, ?)
		""";

	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";

	private static final String SELECT = """
		   SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", prtr."TipoResiduoId", tr."Nombre", pr."CiudadanoId", ciu."UsuarioId"
		     FROM "PuntoReciclaje" AS pr
		LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		    WHERE 1=1
		""";

	private static final String SELECT_GET = """
		SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."CiudadanoId" , prtr."TipoResiduoId", tr."Nombre", ciu."UsuarioId"
		  FROM "PuntoReciclaje" AS pr
		  LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		  LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		  LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		 WHERE pr."ID" = ?
		   AND pr."CiudadanoId" = ?
		""";

	private static final String WHERE_CIUDADADO_NULL = """
		AND pr."CiudadanoId" IS NULL
		""";

	private static final String WHERE_CIUDADADO_NOT_NULL = """
		AND pr."CiudadanoId" IS NOT NULL
		""";

	private static final String WHERE_TIPO = """
		    AND pr."ID" IN (
		    SELECT spr."ID"
		      FROM "PuntoReciclaje" spr
		 LEFT JOIN "PuntoReciclaje_TipoResiduo" AS sprtr ON sprtr."PuntoReciclajeId" = spr."ID"
		 LEFT JOIN "TipoResiduo" AS str on str."ID" = sprtr."TipoResiduoId"
		     WHERE str."ID" IN (%s)
		    )
		""";

	private static final String INSERT_PUNTO_RECICLAJE_TIPO_RESIDUO = """
		INSERT INTO public."PuntoReciclaje_TipoResiduo"("PuntoReciclajeId", "TipoResiduoId")
		VALUES (?, ?);
		""";

	private static final String DELETE = """
		DELETE FROM public."PuntoReciclaje" AS pr
		 WHERE pr."ID" = ?
		   AND pr."CiudadanoId" = ?
		""";

	private static final String DELETE_RELACION = """
		DELETE FROM public."PuntoReciclaje_TipoResiduo" AS r
		 WHERE r."PuntoReciclajeId" = ?
		""";

	private static final String UPDATE = """
		UPDATE public."PuntoReciclaje"
		SET
		""";

	private static final String WHERE_ID_AND_CIUDADANO = """
		   WHERE "ID" = ?
		    	AND "CiudadanoId" = ?
		""";

	@Inject
	PuntoReciclajeDao(DataSource ds) {
		super(ds);
	}


	public PuntoReciclaje save(Transaction t, PuntoReciclaje punto) throws PersistenceException {

		try ( var insert = t.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS) ) {
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
	public List<PuntoReciclaje> list(PuntoReciclajeFilter filter) throws PersistenceException {
		try (var t = open(true); var select = createSelectForList(t, filter)) {
			try (var rs = select.executeQuery()) {
				return getList(rs, filter);
			}
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	/**
	 * Obtiene un punto de reciclaje por medio de su id
	 */
	public Optional<PuntoReciclaje> get(Long id, Long recicladorId) throws PersistenceException {
		try (var t = open(true); var select = createSelectForGet(t, id, recicladorId);
			 var rs = select.executeQuery()) {
			return Optional.of(getPunto(rs));
		} catch (SQLException e) {
			throw new PersistenceException("error getting punto reciclaje", e);
		}
	}

	/**
	 * Actualiza un punto de reciclaje
	 * @param id
	 * @param recicladorId
	 * @return
	 * @throws PersistenceException
	 */
	public boolean put(Transaction t, Long id, Long recicladorId, PuntoReciclajeRequest req) throws SQLException, PersistenceException {

		try (var update = createSelectForPut(t, id, recicladorId, req)) {

			if (update.executeUpdate() <= 0)
				return false;

			return true;

		} catch (SQLException | PersistenceException e) {
			throw new PersistenceException("error updating PuntoReciclaje", e);
		}
	}

	/**
	 * Procesa el resultado de la consulta de list
	 * Agrupa los tipos de residuo por cada punto
	 * Valida el filtro de dias
	 */
	private List<PuntoReciclaje> getList(ResultSet rs, PuntoReciclajeFilter filter) throws SQLException {
		var mapPuntos = new HashMap<Long, PuntoReciclaje>();
		while (rs.next()) {
			val id = rs.getLong("ID");
			PuntoReciclaje punto = mapPuntos.get(id);
			if (punto == null) {
				punto = new PuntoReciclaje(
					id,
					rs.getString("Titulo"),
					rs.getDouble("Latitud"),
					rs.getDouble("Longitud"),
					Dia.getDia(rs.getString("DiasAbierto")),
					new ArrayList<>(),
					rs.getLong("CiudadanoId"),
					User.builder().id(rs.getLong("UsuarioId")).build()
				);
				mapPuntos.put(id, punto);
			}
			if (rs.getInt("TipoResiduoId") != 0)
				punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}

		return mapPuntos.values().stream()
			.filter(p -> !filter.hasDias() || !Collections.disjoint(filter.dias, p.dias))
			.toList();
	}

	/**
	 * Procesa el resultado de la consulta de get
	 */
	private PuntoReciclaje getPunto(ResultSet rs) throws SQLException {
		PuntoReciclaje punto = null;
		if (rs.next()) {
			punto = new PuntoReciclaje(
				rs.getLong("ID"),
				rs.getString("Titulo"),
				rs.getDouble("Latitud"),
				rs.getDouble("Longitud"),
				Dia.getDia(rs.getString("DiasAbierto")),
				new ArrayList<>(),
				rs.getLong("CiudadanoId"),
				User.builder().id(rs.getLong("UsuarioId")).build()
			);
			if (rs.getInt("TipoResiduoId") != 0)
				punto.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("Nombre")));
		}
		return punto;
	}

	/**
	 *
	 */
	private PreparedStatement createSelectForList(Transaction t, PuntoReciclajeFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		List<Object> parameters = new ArrayList<>();

		b.append(f.isPuntoVerde() ? WHERE_CIUDADADO_NULL : WHERE_CIUDADADO_NOT_NULL);

		if (f.hasTipo()) {
			String marks = f.tiposResiduos.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			b.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tiposResiduos);
		}

		if (f.hasReciclador()) {
			b.append("AND pr.\"CiudadanoId\" = ?\n");
			parameters.add(f.reciclador_id);
		}

		if (f.hasArea()) {
			b.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	private PreparedStatement createSelectForGet(Transaction t, Long id, Long recicladorId) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT_GET);
		List<Object> parameters = new ArrayList<>();

		parameters.add(id);
		parameters.add(recicladorId);

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	public void saveRelacion(Transaction t, long trId, long prId) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT_PUNTO_RECICLAJE_TIPO_RESIDUO, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, prId);
			insert.setLong(2, trId);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the relation between puntoReciclaje and tipoResiduo failed," +
					" no affected rows");

		} catch (SQLException | PersistenceException e ) {
			throw new PersistenceException("error creating the relation between puntoReciclaje and tipoResiduo", e);
		}
	}

	public boolean delete(Transaction t, Long id, Long recicladorId) throws PersistenceException, SQLException {

		try (var delete = t.prepareStatement(DELETE, Statement.RETURN_GENERATED_KEYS)) {
			delete.setLong(1, id);
			delete.setLong(2, recicladorId);

			if (delete.executeUpdate() <= 0 )
				return false;

			return true;

		} catch (SQLException | PersistenceException e) {
			throw new PersistenceException("error deleting puntoReciclaje", e);
		}
	}

	public boolean deleteRelacion(Transaction t, Long id) throws PersistenceException, SQLException {

		try (var delete = t.prepareStatement(DELETE_RELACION, Statement.RETURN_GENERATED_KEYS)) {
			delete.setLong(1, id);

			if (delete.executeUpdate() <= 0)
				return false;

			return true;

		} catch (SQLException | PersistenceException e) {
			throw new PersistenceException("error deleting the relation between puntoReciclaje and tipoResiduo", e);
		}
	}

	private PreparedStatement createSelectForPut(Transaction t, Long id, Long recicladorId, PuntoReciclajeRequest req) throws PersistenceException, SQLException {

		var b = new StringBuilder(UPDATE);
		List<Object> parameters = new ArrayList<>();
		List<String> setFragments = new ArrayList<>();

		if(req.latitud != null){
			setFragments.add("\"Latitud\"=? ");
			parameters.add(req.latitud);
		}

		if (req.longitud != null) {
			setFragments.add("\"Longitud\"=? ");
			parameters.add(req.longitud);
		}

		if (!req.dias.isEmpty()) {
			setFragments.add("\"DiasAbierto\"=? ");
			parameters.add(Dia.getDias(Dia.getDia(req.dias)));
		}

		if (req.titulo != null) {
			setFragments.add("\"Titulo\"=? ");
			parameters.add(req.titulo);
		}

		b.append(String.join(", ", setFragments))
			.append(System.lineSeparator())
			.append(WHERE_ID_AND_CIUDADANO);

		parameters.add(id);
		parameters.add(recicladorId);

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;

	}
}
