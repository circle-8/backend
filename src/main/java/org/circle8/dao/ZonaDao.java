package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Ciudadano;
import org.circle8.entity.Organizacion;
import org.circle8.entity.Punto;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Retiro;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.User;
import org.circle8.entity.Zona;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.ZonaExpand;
import org.circle8.filter.ZonaFilter;
import org.circle8.utils.Dates;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;

import lombok.val;

public class ZonaDao extends Dao {

	private static final Gson GSON = new Gson();
	
	private static final String INSERT = """
			INSERT INTO "Zona"(
			"OrganizacionId", "Polyline", "Nombre")
			VALUES (?, ?, ?);
			""";
	
	private static final String INSERT_ZONA_TIPO_RESIDUO = """
			INSERT INTO "Zona_TipoResiduo"("ZonaId", "TipoResiduoId")
			VALUES (?, ?);
			""";
	
	private static final String UPDATE_ZONA = """
			UPDATE public."Zona"
			SET "Polyline"=?, "Nombre"=?
			WHERE "ID"=?;
			""";

	private static final String INSERT_INTO_PUNTO_RESIDUO_ZONA = """
			INSERT INTO "PuntoResiduo_Zona" ("PuntoResiduoId", "ZonaId")
			VALUES (?, ?);
			""";

	private static final String DELETE_PUNTO_ZONA = """
			DELETE FROM "PuntoResiduo_Zona"
			WHERE "PuntoResiduoId" = ? AND "ZonaId" = ?;
			""";

	private static final String SELECT_FMT = """
			SELECT
			       %s
			  FROM "Zona" AS z
			  LEFT JOIN "Zona_TipoResiduo" AS ztr ON ztr."ZonaId" = z."ID"
			  LEFT JOIN "TipoResiduo" AS tr on tr."ID" = ztr."TipoResiduoId"
			    %s
			 WHERE 1 = 1
			""";
	
	private static final String SELECT_PUNTOS_IN_ZONA = """
			SELECT pr."ID",pr."CiudadanoId", pr."Latitud", pr."Longitud"
			FROM "PuntoResiduo_Zona" pz
			LEFT JOIN "PuntoResiduo" AS pr on pr."ID" = pz."PuntoResiduoId"
			WHERE pz."ZonaId" = ?;
			""";

	private static final String SELECT_SIMPLE = """
			z."ID", z."OrganizacionId", z."Polyline", z."Nombre", ztr."TipoResiduoId" , tr."Nombre" as tipoResiduoNombre
			""";

	private static final String SELECT_ORGANIZACION = """
			, org."RazonSocial", org."UsuarioId" as usuarioOrgId
			""";

	private static final String SELECT_RECORRIDO = """
			, rec."ID" as recorridoId, rec."FechaRetiro", rec."FechaInicio", rec."FechaFin", rec."RecicladorId"
			""";

	private static final String SELECT_RECICLADOR = """
			, reciUrb."UsuarioId" as usuarioRecicladorId
			""";

	private static final String SELECT_CIUDADANO = """
			, ciu."UsuarioId" as usuarioCiudadanoId
			""";

	private static final String SELECT_PUNTO_RESIDUO = """
			, pr."ID" AS puntoResiduoID , pr."CiudadanoId", pr."Latitud", pr."Longitud"
			""";

	private static final String JOIN_ORGANIZACION = """
			JOIN "Organizacion" AS org on org."ID" = z."OrganizacionId"
			""";

	private static final String JOIN_RECORRIDO = """
			LEFT JOIN "Recorrido" AS rec on rec."ZonaId" = z."ID"
			LEFT JOIN "RecicladorUrbano" AS reciUrb on reciUrb."ID" = rec."RecicladorId"
			""";

	private static final String JOIN_RECICLADOR = """
			LEFT JOIN "RecicladorUrbano" AS reci on z."ID" = reci."ZonaId"
			""";

	private static final String JOIN_PUNTO_RESIDUO_ZONA = """
			LEFT JOIN "PuntoResiduo_Zona" AS prz on z."ID" = prz."ZonaId"
			""";

	private static final String JOIN_PUNTO_RESIDUO = """
			LEFT JOIN "PuntoResiduo_Zona" AS prz2 on z."ID" = prz2."ZonaId"
			LEFT JOIN "PuntoResiduo" AS pr on pr."ID" = prz2."PuntoResiduoId"
			""";

	private static final String JOIN_PUNTO_RESIDUO_CIUDADANO = """
			LEFT JOIN "Ciudadano" AS ciu on ciu."ID" = pr."CiudadanoId"
			""";

	private static final String WHERE_ORGANIZACION = """
			AND z."OrganizacionId" = ?
			""";

	private static final String WHERE_RECICLADOR = """
			AND reci."ID" = ?
			""";

	private static final String WHERE_PUNTO_RESIDUO = """
			AND prz."PuntoResiduoId" = ?
			""";

	private static final String WHERE_CIUDADANO = """
			AND pr."CiudadanoId" = ?
			""";

	private static final String WHERE_ID = """
			AND z."ID" = ?
			""";

	private static final String WHERE_TIPO_RESIDUO = """
		AND z."ID" IN (
		    SELECT sz."ID"
		      FROM "Zona" sz
		 LEFT JOIN "Zona_TipoResiduo" AS sztr ON sztr."ZonaId" = sz."ID"
		 LEFT JOIN "TipoResiduo" AS str on str."ID" = sztr."TipoResiduoId"
		     WHERE str."ID" IN (%s)
		)
		""";
	
	private static final String DELETE_ZONA_TIPO = """
			DELETE FROM "Zona_TipoResiduo"
			WHERE "ZonaId" = ?;
			""";


	@Inject
	ZonaDao(DataSource ds) {
		super(ds);
	}
	
	public Zona save(Transaction t,Long organizacionId,Zona zona) throws PersistenceException {		
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, organizacionId);
			insert.setString(2, getPolyline(zona.polyline));
			insert.setString(3, zona.nombre);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the Zona failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					zona.id = rs.getLong(1);
				else
					throw new SQLException("Creating the Zona failed, no ID obtained");
			}
		} catch ( SQLException e ) {
				throw new PersistenceException("error inserting Zona", e);
		}

		return zona;
	}
	
	public Zona update(Transaction t,Long zonaId,Zona zona) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(UPDATE_ZONA) ) {
			put.setString(1, getPolyline(zona.polyline));
			put.setString(2, zona.nombre);
			put.setLong(3, zonaId);
			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe zona con id " + zonaId);
		} catch ( SQLException e ) {
				throw new PersistenceException("error inserting Zona", e);
		}
		return zona;
	}
	
	public void saveTipos(Transaction t, long zonaId, long tipoResiduoId) throws PersistenceException, NotFoundException {
		try ( var insert = t.prepareStatement(INSERT_ZONA_TIPO_RESIDUO, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, zonaId);
			insert.setLong(2, tipoResiduoId);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new NotFoundException("No existe el TipoResiduo a actualizar.");

		} catch (SQLException e ) {	
			if ( e.getMessage().contains("TipoResiduo_fkey"))
				throw new NotFoundException("No existe el TipoResiduo a con id " + tipoResiduoId);
			
			throw new PersistenceException("error creating the relation between zona and tipoResiduo.", e);
		}
	}
	
	public void deleteTipos(Transaction t,Long zonaId) throws NotFoundException, PersistenceException {
		try ( val delete =  t.prepareStatement(DELETE_ZONA_TIPO) ) {
			delete.setLong(1, zonaId);
			if ( delete.executeUpdate() <= 0 )
				throw new NotFoundException("No se encontro zona para eliminar");
		} catch (SQLException e) {			
			throw new PersistenceException("error Deleting tipos in zona", e);
		}
	}	

	public void includePuntoResiduo(Transaction t,Long puntoResiduoId, Long zonaId) throws PersistenceException {
		try ( val insert = t.prepareStatement(INSERT_INTO_PUNTO_RESIDUO_ZONA, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setLong(1, puntoResiduoId);
			insert.setLong(2, zonaId);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the punto residuo zona failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( !rs.next() )
					throw new SQLException("Creating the punto residuo zona failed, no ID obtained");
			}
		} catch (SQLException e) {
			if ( e.getMessage().contains("PuntoResiduo_Zona_pkey") )
				throw new DuplicatedEntry("punto residuo zona already exist", e);

			throw new PersistenceException("error inserting punto de residuo in zona", e);
		}
	}

	public void excludePuntoResiduo(Transaction t,Long puntoResiduoId, Long zonaId) throws PersistenceException, NotFoundException {
		try ( val delete =  t.prepareStatement(DELETE_PUNTO_ZONA) ) {
			delete.setLong(1, puntoResiduoId);
			delete.setLong(2, zonaId);

			if ( delete.executeUpdate() <= 0 )
				throw new NotFoundException("No se encontro el punto en la zona para eliminar");
		} catch (SQLException e) {
			throw new PersistenceException("error Deleting punto de residuo in zona", e);
		}
	}

	public Optional<Zona> get(Transaction t, ZonaFilter f, ZonaExpand x) throws PersistenceException {
		try ( val select = createSelect(t, f, x) ) {
			try ( var rs = select.executeQuery() ) {
				return Optional.ofNullable(getZona(rs, x));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting solicitud", e);
		}
	}

	public List<Zona> list(ZonaFilter f, ZonaExpand x) throws PersistenceException {
		try ( var t = open(true) ) {
			return list(t, f, x);
		}
	}

	public List<Zona> list(Transaction t, ZonaFilter f, ZonaExpand x) throws PersistenceException {
		try (
			 var select = createSelect(t, f, x);
			 var rs = select.executeQuery()
		) {
			return getList(rs, x);
		} catch (SQLException e) {
			throw new PersistenceException("error getting zonas", e);
		}
	}
	
	public List<PuntoResiduo> getPuntosResiduo(Transaction t, Long zonaId) throws PersistenceException {
		try(var select = createSelectListPuntos(t, zonaId);
			var rs = select.executeQuery()) {
			var puntos = new ArrayList<PuntoResiduo>();
			while (rs.next()) {
				val punto = PuntoResiduo.builder()
						.id(rs.getLong("ID"))
						.ciudadanoId(rs.getLong("CiudadanoId"))
						.latitud(rs.getDouble("Latitud"))
						.longitud(rs.getDouble("Longitud"))
						.build();
				puntos.add(punto);				
			}
			return puntos;
		} catch (Exception e) {
			throw new PersistenceException("error getting puntos in zona", e);
		}
	}
	
	private PreparedStatement createSelectListPuntos(
			Transaction t,
			Long zonaId
		) throws PersistenceException, SQLException {
			var p = t.prepareStatement(SELECT_PUNTOS_IN_ZONA);
			p.setLong(1, zonaId);
			return p;
		}

	private PreparedStatement createSelect(
		Transaction t,
		ZonaFilter f,
		ZonaExpand x
	) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		var joinFields = "";
		if(x.organizacion) {
			selectFields += SELECT_ORGANIZACION;
			joinFields += JOIN_ORGANIZACION;
		}

		if(x.recorridos) {
			selectFields += SELECT_RECORRIDO + SELECT_RECICLADOR;
			joinFields += JOIN_RECORRIDO;
		}

		if(x.puntosResiduo) {
			selectFields += SELECT_PUNTO_RESIDUO + SELECT_CIUDADANO;
		}

		if(f.recicladorId != null) {
			joinFields += JOIN_RECICLADOR;
		}

		if(f.puntoResiduoId != null) {
			joinFields += JOIN_PUNTO_RESIDUO_ZONA;
		}

		if(f.ciudadanoId != null || x.puntosResiduo) {
			joinFields += JOIN_PUNTO_RESIDUO;
			if(x.puntosResiduo)
				joinFields += JOIN_PUNTO_RESIDUO_CIUDADANO;
		}

		var sql = String.format(SELECT_FMT, selectFields, joinFields);
		var b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();

		if(f.id != null) {
			b.append(WHERE_ID);
			parameters.add(f.id);
		}

		if(f.organizacionId != null) {
			b.append(WHERE_ORGANIZACION);
			parameters.add(f.organizacionId);
		}

		if ( f.hasTipo() ) {
			appendListCondition(f.tiposResiduos, WHERE_TIPO_RESIDUO, b, parameters);
		}

		if(f.recicladorId != null) {
			b.append(WHERE_RECICLADOR);
			parameters.add(f.recicladorId);
		}

		if(f.puntoResiduoId != null) {
			b.append(WHERE_PUNTO_RESIDUO);
			parameters.add(f.puntoResiduoId);
		}

		if(f.ciudadanoId != null) {
			b.append(WHERE_CIUDADANO);
			parameters.add(f.ciudadanoId);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	private Zona getZona(ResultSet rs, ZonaExpand x) throws SQLException {
		Zona z = null;
		boolean zonaCreada = false;
		while (rs.next()) {
			if (!zonaCreada) {
				z = buildZona(rs, x);
				zonaCreada = true;
			}
			addTipoResiduo(rs, z);
			addRecorrido(rs, x.recorridos, z);
			addPuntoResiduo(rs, x.puntosResiduo, z);
		}
		return z;
	}

	private List<Zona> getList(ResultSet rs, ZonaExpand x) throws SQLException {
		var mapZonas = new HashMap<Long, Zona>();
		while (rs.next()) {
			val id = rs.getLong("ID");
			Zona z = mapZonas.get(id);
			if (z == null) {
				z = buildZona(rs, x);
				mapZonas.put(id, z);
			}
			addTipoResiduo(rs, z);
			addRecorrido(rs, x.recorridos, z);
			addPuntoResiduo(rs, x.puntosResiduo, z);
		}
		return mapZonas.values().stream().toList();
	}

	private Zona buildZona(ResultSet rs, ZonaExpand x) throws SQLException {
		var z = new Zona();
		z.id = rs.getLong("ID");
		z.nombre = rs.getString("Nombre");
		z.polyline = getPolyline(rs.getString("Polyline"));
		z.organizacionId = rs.getLong("OrganizacionId");
		z.organizacion = buildOrganizacion(rs, x.organizacion);
		z.tipoResiduo = new ArrayList<>();
		z.recorridos = new ArrayList<>();
		z.puntosResiduos = new ArrayList<>();
		return z;
	}

	List<Punto> getPolyline(String poly) {
		val l = new ArrayList<Punto>();
		float[][] list = GSON.fromJson(poly, float[][].class);
		for (float[] element : list) {
			l.add(new Punto(element[0], element[1]));
		}
		return l;
	}
	
	private String getPolyline(List<Punto> puntos) {
		JsonArray array = new JsonArray();
		for(Punto punto : puntos) {
			JsonArray puntoArray = new JsonArray();
			puntoArray.add(new JsonPrimitive(punto.latitud));
			puntoArray.add(new JsonPrimitive(punto.longitud));
			array.add(puntoArray);
		}
		return GSON.toJson(array);
	}

	private void addTipoResiduo(ResultSet rs, Zona z) throws SQLException {
		if (rs.getInt("TipoResiduoId") != 0) {
			val tr = new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("tipoResiduoNombre"));
			if(!z.tipoResiduo.contains(tr))
				z.tipoResiduo.add(tr);
		}

	}

	private Organizacion buildOrganizacion(ResultSet rs, boolean expand) throws SQLException {
		if(!expand)
			return Organizacion.builder()
					.id(rs.getLong("OrganizacionId"))
					.build();

		return Organizacion.builder()
				.id(rs.getLong("OrganizacionId"))
				.razonSocial(rs.getString("RazonSocial"))
				.usuarioId(rs.getLong("usuarioOrgId"))
				.build();
	}

	private void addRecorrido(ResultSet rs, boolean expand, Zona z) throws SQLException {
		if(expand && rs.getInt("recorridoId") != 0) {
			val rec = new Recorrido();
			rec.id = rs.getInt("recorridoId");
			rec.fechaRetiro = rs.getDate("FechaRetiro").toLocalDate();
			rec.recicladorId = rs.getLong("RecicladorId");
			rec.reciclador = new Ciudadano(rs.getLong("RecicladorId"), rs.getLong("usuarioRecicladorId"));
			rec.puntos = new ArrayList<Retiro>();
			if(rs.getTimestamp("FechaInicio") != null)
				rec.fechaInicio = rs.getTimestamp("FechaInicio").toInstant().atZone(Dates.UTC);
			if(rs.getTimestamp("FechaFin") != null)
				rec.fechaFin = rs.getTimestamp("FechaFin").toInstant().atZone(Dates.UTC);

			if(!z.recorridos.contains(rec))
				z.recorridos.add(rec);
		}
	}

	private void addPuntoResiduo(ResultSet rs, boolean expand, Zona z) throws SQLException {
		if(expand && rs.getInt("puntoResiduoID") != 0) {
			val pr = new PuntoResiduo(rs.getLong("puntoResiduoID"), rs.getLong("CiudadanoId"));
			pr.latitud = rs.getDouble("latitud");
			pr.longitud = rs.getDouble("longitud");
			pr.ciudadano = User.builder().id(rs.getLong("usuarioCiudadanoId")).build();
			if(!z.puntosResiduos.contains(pr))
				z.puntosResiduos.add(pr);
		}
	}
}
