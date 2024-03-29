package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dto.Dia;
import org.circle8.entity.Ciudadano;
import org.circle8.entity.PuntoReciclaje;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Residuo;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Transaccion;
import org.circle8.entity.Transporte;
import org.circle8.entity.Transportista;
import org.circle8.entity.User;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.TransaccionExpand;
import org.circle8.filter.TransaccionFilter;
import org.circle8.utils.Dates;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TransaccionDao extends Dao {

	private static final String INSERT_SQL = """
		INSERT INTO "TransaccionResiduo"("FechaPrimerContacto", "PuntoReciclajeId")
		VALUES (?, ?);
		""";

	private static final String UPDATE_RESIDUO_ADD_TRANSACCION_ID = """
		UPDATE "Residuo" as r
		SET "TransaccionId" = ?
		WHERE r."ID"= ?;
		""";

	private static final String UPDATE_RESIDUOS_REMOVE_TRANSACCION_ID = """
		UPDATE "Residuo" as r
		SET "TransaccionId" = NULL
		WHERE r."TransaccionId"= ?;
		""";

	private static final String UPDATE_RESIDUO_REMOVE_TRANSACCION_ID = """
		UPDATE "Residuo" as r
		SET "TransaccionId" = NULL
		WHERE r."ID"= ?
		AND r."TransaccionId"= ?;
		""";

	private static final String DELETE = """
		DELETE FROM "TransaccionResiduo" AS tr
		WHERE tr."ID"= ?;
		""";

	private static final String UPDATE_TRANSACCION_REMOVE_TRANSPORTE_ID = """
		UPDATE "TransaccionResiduo" as tr
		SET "TransporteId" = NULL
		WHERE tr."ID"= ?
		AND tr."TransporteId"= ?;
		""";

	private static final String UPDATE_TRANSACCION_ADD_TRANSPORTE_ID = """
		UPDATE "TransaccionResiduo" as tr
		SET "TransporteId" = ?
		WHERE tr."ID"= ?;
		""";

	private static final String PUT = """
		UPDATE "TransaccionResiduo" as tr
		SET "FechaEfectiva"=?
		WHERE tr."ID"=?;
		""";

	private static final String SELECT_TRANSPORTE_ID = """
		SELECT "TransporteId"
		FROM "TransaccionResiduo" AS tr
		WHERE tr."ID"= ?;
		""";

	private static final String SELECT_FMT = """
		SELECT
		%s
		FROM "TransaccionResiduo" AS tr
		JOIN "PuntoReciclaje" AS pr on tr."PuntoReciclajeId"= pr."ID"
		JOIN "Ciudadano" AS c1 on c1."ID" = pr."CiudadanoId"
		JOIN "Usuario" AS u1 on u1."ID" = c1."UsuarioId"
		%s
		WHERE 1 = 1
		""";

	private static final String SELECT_SIMPLE = """
		tr."ID", tr."FechaPrimerContacto", tr."FechaEfectiva", tr."PuntoReciclajeId",
		pr."CiudadanoId", c1."UsuarioId", u1."NombreApellido", u1."Username",
		tr."TransporteId"
		""";

	private static final String SELECT_RESIDUOS = """
		, re."ID" AS residuoId, re."FechaCreacion", re."FechaRetiro", re."PuntoResiduoId", re."Descripcion", re."FechaLimiteRetiro", re."Base64",
		re."TipoResiduoId", re."RecorridoId", tre."Nombre" AS TipoResiduoNombre,
		puntre."CiudadanoId" AS ciudadanoPuntoResiduo, c."UsuarioId" AS UsuarioPuntoResiduo,
		ures."NombreApellido" AS NombreApellidoPuntoResiduo, ures."Username" AS UsernamePuntoResiduo,
		puntre."Latitud" as latitudPuntoResiduo, puntre."Longitud" as longitudPuntoResiduo
		""";

	private static final String SELECT_TRANSPORTE = """
		, tra."FechaAcordada", tra."FechaInicio", tra."FechaFin", tra."Precio", tra."TransportistaId",
		tra."PagoConfirmado", tra."EntregaConfirmada", tra."PrecioSugerido",
		transportista."UsuarioId" AS UsuarioTransportista, utrans."NombreApellido" AS NombreTransportista,
		utrans."Username" AS UsernameTransportista
		""";

	private static final String SELECT_PUNTO_RECICLAJE = """
		, pr."Titulo", pr."Latitud", pr."Longitud", pr."DiasAbierto"
		""";

	private static final String JOIN_RESIDUOS = """
		LEFT JOIN "Residuo" AS re on tr."ID" = re."TransaccionId"
		LEFT JOIN "TipoResiduo" AS tre on re."TipoResiduoId" = tre."ID"
		LEFT JOIN "PuntoResiduo" AS puntre on puntre."ID" = re."PuntoResiduoId"
		LEFT JOIN "Ciudadano" AS c on c."ID" = puntre."CiudadanoId"
		LEFT JOIN "Usuario" AS ures on ures."ID" = c."UsuarioId"
		""";

	private static final String JOIN_TRANSPORTE = """
		LEFT JOIN "Transporte" AS tra on tr."TransporteId" = tra."ID"
		LEFT JOIN "Transportista" AS transportista ON transportista."ID" = tra."TransportistaId"
		LEFT JOIN "Usuario" AS utrans ON utrans."ID" = transportista."UsuarioId"
		""";

	private static final String WHERE_ID = """
		AND tr."ID" = ?
		""";

	private static final String WHERE_PUNTO_RECICLAJE = """
		AND pr."ID" IN ( %s )
		""";

	private static final String WHERE_TRANSPORTISTA = """
		AND tra."TransportistaId" = ?
		""";
	
	private static final String WHERE_TRANSPORTISTA_NULL = """
		AND tr."TransporteId" is NULL
		""";
	
	private static final String WHERE_TRANSPORTISTA_NOT_NULL = """
		AND tr."TransporteId" is not NULL
		""";

	private static final String WHERE_CIUDADANO = """
		AND pr."CiudadanoId" = ?
		""";
	
	@Inject
	TransaccionDao(DataSource ds) {
		super(ds);
	}

	public List<Transaccion> list(TransaccionFilter filter, TransaccionExpand exp) throws PersistenceException {
		try ( val t = open() ) {
			return list(t, filter, exp);
		}
	}

	public List<Transaccion> list(Transaction t, TransaccionFilter filter, TransaccionExpand exp) throws PersistenceException {
		try ( val select = createSelect(t, filter, exp); val rs = select.executeQuery() ) {
			val mapTransacciones = new HashMap<Long, Transaccion>();
			while (rs.next()) {
				val id = rs.getLong("ID");
				Transaccion transaccion = mapTransacciones.get(id);
				if (transaccion == null) {
					transaccion = buildTransaccion(rs, exp);
					mapTransacciones.put(id, transaccion);
				} else if (exp.residuos)
					transaccion.residuos.add(buildResiduo(rs, exp.residuosBase64));
			}
			return mapTransacciones.values().stream().toList();
		} catch (SQLException e) {
			throw new PersistenceException("error getting Transacciones", e);
		}
	}

	public Optional<Transaccion> get(long transaccionId) throws PersistenceException {
		try (var t = open(true)) {
			return get(t, transaccionId, new TransaccionExpand(new ArrayList<>()));
		}
	}

	public Optional<Transaccion> get(long transaccionId, TransaccionExpand expand) throws PersistenceException {
		try (var t = open(true)) {
			return get(t, transaccionId, expand);
		}
	}

	public Optional<Transaccion> get(Transaction t, Long id, TransaccionExpand expand) throws PersistenceException {
		val f = TransaccionFilter.builder().id(id).build();
		try (val ps = createSelect(t, f, expand)) {
			ps.setLong(1, id);
			try (val rs = ps.executeQuery()) {
				if (!rs.next()) {
					return Optional.empty();
				}
				Transaccion tr = buildTransaccion(rs, expand);
				while (rs.next()) {
					if (expand.residuos)
						tr.residuos.add(buildResiduo(rs, expand.residuosBase64));
				}
				return Optional.of(tr);
			}
		} catch (SQLException e) {
			throw new PersistenceException("error selecting transaccion", e);
		}
	}

	private Transaccion buildTransaccion(ResultSet rs, TransaccionExpand expand) throws SQLException {
		val transporte = buildTransporte(rs, expand.transporte);
		val puntoReciclaje = buildPuntoReciclaje(rs, expand.puntoReciclaje);
		List<Residuo> residuos = new ArrayList<>();

		val creacionTimestamp = rs.getTimestamp("FechaPrimerContacto");
		val retiroTimestamp = rs.getTimestamp("FechaEfectiva");

		val tr = new Transaccion(rs.getLong("ID"),
			creacionTimestamp != null ? creacionTimestamp.toInstant().atZone(Dates.UTC) : null,
			retiroTimestamp != null ? retiroTimestamp.toInstant().atZone(Dates.UTC) : null,
			rs.getLong("TransporteId") != 0 ? rs.getLong("TransporteId") : null,
			transporte,
			rs.getLong("PuntoReciclajeId"),
			puntoReciclaje,
			residuos);

		Residuo r;
		if ( expand.residuos && (r = buildResiduo(rs, expand.residuosBase64)) != null )
			residuos.add(r);

		return tr;
	}

	private PuntoReciclaje buildPuntoReciclaje(ResultSet rs, boolean expand) throws SQLException {
		if ( !expand ) {
			return PuntoReciclaje.builder()
				.id(rs.getLong("PuntoReciclajeId"))
				.recicladorId(rs.getLong("CiudadanoId"))
				.reciclador(User.builder()
					.id(rs.getLong("UsuarioId"))
					.nombre(rs.getString("NombreApellido"))
					.username(rs.getString("Username"))
					.ciudadanoId(rs.getLong("CiudadanoId"))
					.build())
				.build();
		}

		return new PuntoReciclaje(rs.getLong("PuntoReciclajeId"),
				rs.getString("Titulo"), rs.getDouble("Latitud"), rs.getDouble("Longitud"),
				Dia.getDia(rs.getString("DiasAbierto")),
				new ArrayList<>(), rs.getLong("CiudadanoId"), null, "");
	}

	private PuntoResiduo buildPuntoResiduo(ResultSet rs) throws SQLException {
		if(rs.getLong("PuntoResiduoId") == 0)
			return null;

		return PuntoResiduo.builder()
				.id(rs.getLong("PuntoResiduoId"))
				.ciudadanoId(rs.getLong("ciudadanoPuntoResiduo"))
				.latitud(rs.getDouble("latitudPuntoResiduo"))
				.longitud(rs.getDouble("longitudPuntoResiduo"))
				.build();
	}

	private Transporte buildTransporte(ResultSet rs, boolean expand) throws SQLException {
		if (!expand || rs.getLong("TransporteId") == 0)
			return null;

		val fechaAcordada = rs.getDate("FechaAcordada");
		val fechaInicioTimestamp = rs.getTimestamp("FechaInicio");
		val fechaFinTimestamp = rs.getTimestamp("FechaFin");
		return new Transporte(rs.getLong("TransporteId"),
				fechaAcordada != null ? fechaAcordada.toLocalDate() : null,
			fechaInicioTimestamp != null ? fechaInicioTimestamp.toInstant().atZone(Dates.UTC) : null,
			fechaFinTimestamp != null ? fechaFinTimestamp.toInstant().atZone(Dates.UTC) : null,
			rs.getBigDecimal("Precio"),
			rs.getLong("TransportistaId"),
			Transportista.builder()
				.id(rs.getLong("TransportistaId"))
				.usuarioId(rs.getLong("UsuarioTransportista"))
				.user(User.builder()
					.id(rs.getLong("UsuarioTransportista"))
					.nombre(rs.getString("NombreTransportista"))
					.username(rs.getString("UsernameTransportista"))
					.build())
				.build(),
			rs.getLong("ID"),
			null,
			rs.getBoolean("PagoConfirmado"),
			rs.getBoolean("EntregaConfirmada"),
			rs.getBigDecimal("PrecioSugerido"));
	}

	private Residuo buildResiduo(ResultSet rs, boolean expandBase64) throws SQLException {
		var id = rs.getLong("ResiduoId");
		if ( id == 0 )
			return null;

		val limit = rs.getTimestamp("FechaLimiteRetiro");
		val retiro = rs.getTimestamp("FechaRetiro");
		val limitDate = limit != null ? limit.toInstant().atZone(Dates.UTC) : null;
		val retiroDate = retiro != null ? retiro.toInstant().atZone(Dates.UTC) : null;

		byte[] base64 = null;
		if ( expandBase64 )
			base64 = rs.getBytes("Base64");

		var c = Ciudadano.builder()
			.id(rs.getLong("ciudadanoPuntoResiduo"))
			.usuarioId(rs.getLong("UsuarioPuntoResiduo"))
			.nombre(rs.getString("NombreApellidoPuntoResiduo"))
			.username(rs.getString("UsernamePuntoResiduo"))
			.build();

		return Residuo
			.builder()
			.id(id)
			.ciudadano(c)
			.fechaCreacion(rs.getTimestamp("FechaCreacion").toInstant().atZone(Dates.UTC))
			.fechaRetiro(retiroDate)
			.fechaLimiteRetiro(limitDate)
			.tipoResiduo(new TipoResiduo(rs.getLong("TipoResiduoId"), rs.getString("TipoResiduoNombre")))
			.puntoResiduo(buildPuntoResiduo(rs))
			.descripcion(rs.getString("Descripcion"))
			.base64(base64)
			.build();
	}

	private PreparedStatement createSelect(Transaction t, TransaccionFilter f, TransaccionExpand exp) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		if (exp.residuos)
			selectFields += SELECT_RESIDUOS;
		if (exp.transporte || f.transportistaId != null)
			selectFields += SELECT_TRANSPORTE;
		if (exp.puntoReciclaje || f.hasPuntos())
			selectFields += SELECT_PUNTO_RECICLAJE;

		var joinFields = "";
		if (exp.residuos)
			joinFields += JOIN_RESIDUOS;

		if (exp.transporte || f.transportistaId != null)
			joinFields += JOIN_TRANSPORTE;

		var sql = String.format(SELECT_FMT, selectFields, joinFields);
		var conditions = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		appendCondition(f.id, WHERE_ID, conditions, parameters);
		appendListCondition(f.puntosReciclaje, WHERE_PUNTO_RECICLAJE, conditions, parameters);
		appendCondition(f.transportistaId, WHERE_TRANSPORTISTA, conditions, parameters);
		appendCondition(f.ciudadanoId, WHERE_CIUDADANO, conditions, parameters);
		appendInequality(f.fechaRetiro, "AND tr.\"FechaEfectiva\" %s\n", conditions, parameters);
		
		if(f.conTransporte != null) {
			conditions.append(f.conTransporte ? 
					WHERE_TRANSPORTISTA_NOT_NULL : 
						WHERE_TRANSPORTISTA_NULL);
		}

		var p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
	}

	public Transaccion save(Transaction t, Transaccion transaccion) throws PersistenceException {
		try (var insert = t.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
			insert.setTimestamp(1, Timestamp.from(ZonedDateTime.now(Dates.UTC).toInstant()));
			insert.setLong(2, transaccion.puntoReciclajeId);

			int insertions = insert.executeUpdate();
			if (insertions == 0)
				throw new SQLException("Creating the Transaccion failed, no affected rows");

			try (var rs = insert.getGeneratedKeys()) {
				if (rs.next())
					transaccion.id = rs.getLong(1);
				else
					throw new SQLException("Creating the Transaccion failed, no ID obtained");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error inserting Transaccion", e);
		}

		return transaccion;
	}

	// TODO: esto aca esta mal, deberia ir en el DAO de residuos
	public void saveResiduo(Transaction t, long idResiduo, long idTransaccion) throws PersistenceException, NotFoundException {
		try (var insert = t.prepareStatement(UPDATE_RESIDUO_ADD_TRANSACCION_ID, Statement.RETURN_GENERATED_KEYS)) {
			insert.setLong(1, idTransaccion);
			insert.setLong(2, idResiduo);

			int insertions = insert.executeUpdate();
			if (insertions == 0) {
				throw new NotFoundException("No existe el Residuo a actualizar.");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating the transaccionId on Residuo", e);
		}
	}

	public void put(Transaction t, Transaccion tr) throws PersistenceException, NotFoundException {
		try (var put = t.prepareStatement(PUT)) {
			put.setTimestamp(1, Timestamp.from(tr.fechaRetiro.toInstant()));
			put.setLong(2, tr.id);
			int puts = put.executeUpdate();
			if (puts == 0)
				throw new NotFoundException("No existe la transaccion con id " + tr.id);
		} catch (SQLException e) {
			throw new PersistenceException("error updating transaccion", e);
		}
	}

	public void delete(Transaction t, Long transaccionId) throws PersistenceException, NotFoundException {
		try (var delete = t.prepareStatement(DELETE)) {
			delete.setLong(1, transaccionId);
			int deleted = delete.executeUpdate();
			if (deleted == 0)
				throw new NotFoundException("No existe la transaccion con id " + transaccionId);
		} catch (SQLException e) {
			throw new PersistenceException("error updating transaccion", e);
		}

	}

	public void removeResiduos(Transaction t, Long transaccionId) throws PersistenceException {
		try (var remove = t.prepareStatement(UPDATE_RESIDUOS_REMOVE_TRANSACCION_ID, Statement.RETURN_GENERATED_KEYS)) {
			remove.setLong(1, transaccionId);
			remove.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException("error removing residuos from transaccion", e);
		}
	}

	public void removeResiduo(Transaction t, Long transaccionId, Long residuoId) throws PersistenceException, NotFoundException {
		try (var remove = t.prepareStatement(UPDATE_RESIDUO_REMOVE_TRANSACCION_ID, Statement.RETURN_GENERATED_KEYS)) {
			remove.setLong(1, residuoId);
			remove.setLong(2, transaccionId);

			int removed = remove.executeUpdate();
			if (removed == 0) {
				throw new NotFoundException("No existe el Residuo a actualizar o pertenece a otra transaccion.");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating the removing a Residuo from a Transaccion", e);
		}
	}

	public void removeTransporte(Transaction t, Long id, Long transporteId) throws PersistenceException, NotFoundException {
		try (var remove = t.prepareStatement(UPDATE_TRANSACCION_REMOVE_TRANSPORTE_ID, Statement.RETURN_GENERATED_KEYS)) {
			remove.setLong(1, id);
			remove.setLong(2, transporteId);

			int removed = remove.executeUpdate();
			if (removed == 0) {
				throw new NotFoundException("No existe la Transaccion a actualizar o su transporteID es distinto al brindado");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error updating the removing a Residuo from a Transaccion", e);
		}
	}

	public void setTransporte(Transaction t, Long id, Long transporteId) throws PersistenceException, NotFoundException {
		try (var setTransporte = t.prepareStatement(UPDATE_TRANSACCION_ADD_TRANSPORTE_ID, Statement.RETURN_GENERATED_KEYS)) {
			setTransporte.setLong(1, transporteId);
			setTransporte.setLong(2, id);

			int updated = setTransporte.executeUpdate();
			if (updated == 0) {
				throw new NotFoundException("No existe la Transaccion a actualizar");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error adding a Transporte to a Transaccion", e);
		}
	}

	public boolean hasTransporte(Transaction t, Long id) throws PersistenceException, NotFoundException {
		try (var select = t.prepareStatement(SELECT_TRANSPORTE_ID, Statement.RETURN_GENERATED_KEYS)) {
			select.setLong(1, id);
			var rs = select.executeQuery();
			if (!rs.next()) {
				throw new NotFoundException("No existe la Transaccion a actualizar");
			}
			return rs.getLong("TransporteId") == 0;
		} catch (SQLException e) {
			throw new PersistenceException("error removing a Residuo from a Transaccion", e);
		}
	}
}
