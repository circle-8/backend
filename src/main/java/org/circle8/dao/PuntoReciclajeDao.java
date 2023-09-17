package org.circle8.dao;

import com.google.inject.Inject;
import jakarta.annotation.Nullable;
import lombok.val;
import org.circle8.controller.request.punto_reciclaje.PuntoReciclajePostRequest;
import org.circle8.dto.Dia;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.PuntoReciclajeFilter;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PuntoReciclajeDao extends Dao {

	private static final String INSERT_SQL = """
		INSERT INTO public."PuntoReciclaje"("CiudadanoId", "Latitud", "Longitud", "DiasAbierto", "Titulo")
		VALUES (?, ?, ?, ?, ?)
		""";

	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";

	private static final String SELECT = """
		   SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."Email", prtr."TipoResiduoId", tr."Nombre", pr."CiudadanoId", ciu."UsuarioId"
		     FROM "PuntoReciclaje" AS pr
		LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		    WHERE 1=1
		""";

	private static final String SELECT_GET = """
		SELECT pr."ID", pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto", pr."CiudadanoId", pr."Email", prtr."TipoResiduoId", tr."Nombre", ciu."UsuarioId"
		  FROM "PuntoReciclaje" AS pr
		  LEFT JOIN "PuntoReciclaje_TipoResiduo" AS prtr ON prtr."PuntoReciclajeId" = pr."ID"
		  LEFT JOIN "TipoResiduo" AS tr on tr."ID" = prtr."TipoResiduoId"
		  LEFT JOIN "Ciudadano" AS ciu on pr."CiudadanoId" = ciu."ID"
		 WHERE pr."ID" = ?
		""";
	private static final String WHERE_CIUDADANO = """
		   AND pr."CiudadanoId" = ?
		""";

	private static final String WHERE_CIUDADANO_NULL = """
		AND pr."CiudadanoId" IS NULL
		""";

	private static final String WHERE_CIUDADANO_NOT_NULL = """
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

	public Optional<PuntoReciclaje> get(long id, @Nullable Long recicladorId) throws PersistenceException {
		try (var t = open(true)) {
			return get(t, id, recicladorId);
		}
	}

	public Optional<PuntoReciclaje> get(Transaction t, long id) throws PersistenceException {
		return get(t, id, null);
	}

	public Optional<PuntoReciclaje> get(Transaction t, long id, @Nullable Long recicladorId) throws PersistenceException {
		try ( var select = createSelectForGet(t, id, recicladorId); var rs = select.executeQuery() ) {
			return Optional.ofNullable(getPunto(rs));
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
	public void put(Transaction t, Long id, Long recicladorId, PuntoReciclajePostRequest req) throws PersistenceException, NotFoundException {

		try (var update = createUpdateForPut(t, id, recicladorId, req)) {

			if (update.executeUpdate() <= 0)
				throw new NotFoundException("No se encontro el punto a actualizar.");

		} catch (SQLException e) {
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
					User.builder().id(rs.getLong("UsuarioId")).build(),
					rs.getString("Email")
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
		boolean puntoCreado = false;
		while (rs.next()) {
			if (!puntoCreado) {
				punto = new PuntoReciclaje(
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
						.build(),
					rs.getString("Email")
				);
				puntoCreado = true;
			}
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

		b.append(f.isPuntoVerde() ? WHERE_CIUDADANO_NULL : WHERE_CIUDADANO_NOT_NULL);

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

	private PreparedStatement createSelectForGet(
		Transaction t,
		Long id,
		@Nullable Long recicladorId
	) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT_GET);
		if ( recicladorId != null )
			b.append(WHERE_CIUDADANO);

		var p = t.prepareStatement(b.toString());
		p.setLong(1, id);

		if ( recicladorId != null )
			p.setLong(2, recicladorId);

		return p;
	}

	public void saveTipos(Transaction t, long trId, long prId) throws PersistenceException, NotFoundException {
		try ( var insert = t.prepareStatement(INSERT_PUNTO_RECICLAJE_TIPO_RESIDUO, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, prId);
			insert.setLong(2, trId);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new NotFoundException("No existe el TipoResiduo a actualizar.");

		} catch (SQLException e ) {
			if ( "23506".equals(e.getSQLState()) ) {
				throw new NotFoundException("No existe el TipoResiduo a actualizar.");
			}
			throw new PersistenceException("error creating the relation between puntoReciclaje and tipoResiduo.", e);
		}
	}

	public void delete(Transaction t, Long id, Long recicladorId) throws PersistenceException, NotFoundException {

		try (var delete = t.prepareStatement(DELETE, Statement.RETURN_GENERATED_KEYS)) {
			delete.setLong(1, id);
			delete.setLong(2, recicladorId);

			//TODO Ger quiere probar cositas :)

			if (delete.executeUpdate() <= 0 )
				throw new NotFoundException("No se encontro el punto a eliminar.");

		} catch (SQLException e) {
			throw new PersistenceException("error deleting puntoReciclaje", e);
		}
	}

	public void deleteTipos(Transaction t, Long id) throws PersistenceException, NotFoundException {

		try (var delete = t.prepareStatement(DELETE_RELACION, Statement.RETURN_GENERATED_KEYS)) {
			delete.setLong(1, id);

			if (delete.executeUpdate() <= 0)
				throw new NotFoundException("No se encontro la relacion punto-tipo a eliminar.");

		} catch (SQLException e) {
			throw new PersistenceException("error deleting the relation between puntoReciclaje and tipoResiduo", e);
		}
	}

	private PreparedStatement createUpdateForPut(Transaction t, Long id, Long recicladorId, PuntoReciclajePostRequest req) throws PersistenceException, SQLException {

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

		if (req.dias != null && !req.dias.isEmpty()) {
			setFragments.add("\"DiasAbierto\"=? ");
			parameters.add(Dia.getDias(Dia.getDia(req.dias)));
		}

		if (req.titulo != null) {
			setFragments.add("\"Titulo\"=? ");
			parameters.add(req.titulo);
		}

		b.append(String.join(", ", setFragments))
			.append("\n")
			.append(WHERE_ID_AND_CIUDADANO);

		parameters.add(id);
		parameters.add(recicladorId);

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;

	}
}
