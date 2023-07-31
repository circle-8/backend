package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.circle8.entity.Ciudadano;
import org.circle8.entity.Organizacion;
import org.circle8.entity.Punto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.Retiro;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Zona;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.ZonaFilter;
import org.circle8.utils.Dates;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.inject.Inject;

import lombok.val;

public class ZonaDao extends Dao {
	private static final String SELECT_FMT = """
			SELECT
			       %s
			  FROM "Zona" AS z
			  LEFT JOIN "Zona_TipoResiduo" AS ztr ON ztr."ZonaId" = z."ID"
			  LEFT JOIN "TipoResiduo" AS tr on tr."ID" = ztr."TipoResiduoId"
			    %s
			 WHERE 1 = 1
			""";
	
	private static final String SELECT_SIMPLE = """
			z."ID", z."OrganizacionId", z."Polyline", z."Nombre", ztr."TipoResiduoId" , tr."Nombre" as tipoResiduoNombre
			""";
	
	private static final String SELECT_ORGANIZACION = """
			, org."RazonSocial", org."UsuarioId"
			""";
	
	private static final String SELECT_RECORRIDO = """
			, rec."ID" as recorridoId, rec."FechaRetiro", rec."FechaInicio", rec."FechaFin", rec."RecicladorId"
			""";
	
	private static final String SELECT_RECICLADOR = """
			, reci."UsuarioId"
			""";
	
	private static final String JOIN_ORGANIZACION = """
			JOIN "Organizacion" AS org on org."ID" = z."OrganizacionId"
			""";
	
	private static final String JOIN_RECORRIDO = """
			LEFT JOIN "Recorrido" AS rec on rec."ZonaId" = z."ID"
			""";
	
	private static final String JOIN_RECICLADOR = """
			LEFT JOIN "RecicladorUrbano" AS reci on reci."ID" = rec."RecicladorId"
			""";
	
	private static final String WHERE_ORGANIZACION = """
			AND z."OrganizacionId" = ?
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
	

	@Inject
	ZonaDao(DataSource ds) {
		super(ds);
	}
	
	public Optional<Zona> get(Transaction t, ZonaFilter f) throws PersistenceException {		
		try ( val select = createSelect(t, f) ) {
			try ( var rs = select.executeQuery() ) {				
				return Optional.ofNullable(getZona(rs,f));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting solicitud", e);
		}
	}
	
	public List<Zona> list(ZonaFilter f) throws PersistenceException{
		try (var t = open(true);
			var select = createSelect(t, f);
			var rs = select.executeQuery()
		) {
			return getList(rs,f);
		} catch (SQLException e) {
			throw new PersistenceException("error getting zonas", e);
		}
	}
	
	private PreparedStatement createSelect(
		Transaction t,
		ZonaFilter f
	) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		var joinFields = "";
		if(f.organizacion) {
			selectFields += SELECT_ORGANIZACION;
			joinFields += JOIN_ORGANIZACION;
		}
			
		if(f.recorridos) {
			selectFields += SELECT_RECORRIDO + SELECT_RECICLADOR;
			joinFields += JOIN_RECORRIDO + JOIN_RECICLADOR;
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
			String marks = f.tiposResiduos.stream()
					.map(tr -> "?")
					.collect(Collectors.joining(","));
			b.append(String.format(WHERE_TIPO_RESIDUO, marks));
			parameters.addAll(f.tiposResiduos);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		
		return p;
	}
	
	@NotNull
	private Zona getZona(ResultSet rs, ZonaFilter f) throws SQLException {
		Zona z = null;
		boolean zonaCreada = false;
		while (rs.next()) {
			if (!zonaCreada) {
				z = buildZona(rs, f);
				zonaCreada = true;
			}
			addTipoResiduo(rs, z);
			addRecorrido(rs, f.recorridos, z);
		}
		return z;
	}
	
	private List<Zona> getList(ResultSet rs, ZonaFilter f) throws SQLException {
		var mapZonas = new HashMap<Long, Zona>();
		while (rs.next()) {
			val id = rs.getLong("ID");
			Zona z = mapZonas.get(id);
			if (z == null) {
				z = buildZona(rs, f);
				mapZonas.put(id, z);
			}			
			addTipoResiduo(rs, z);
			addRecorrido(rs, f.recorridos, z);
		}
		return mapZonas.values().stream().toList();
	}
	
	private Zona buildZona(ResultSet rs, ZonaFilter f) throws SQLException {
		var z = new Zona();
		z.id = rs.getLong("ID");
		z.nombre = rs.getString("Nombre");
		z.polyline = getPolyline(rs.getString("Polyline"));
		z.organizacionId = rs.getLong("OrganizacionId");
		z.organizacion = buildOrganizacion(rs, f.organizacion);
		z.tipoResiduo = new ArrayList<TipoResiduo>();
		z.recorridos = new ArrayList<Recorrido>();
		return z;
	}
	
	private  List<Punto> getPolyline(String poly){
		val l = new ArrayList<Punto>();
		Gson gson = new Gson();
		float[][] list = gson.fromJson(poly, float[][].class);
		for (float[] element : list) {
            l.add(new Punto(element[0], element[1]));
        }		
		return l;
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
				.usuarioId(rs.getLong("UsuarioId"))
				.build();
	}
	
	private void addRecorrido(ResultSet rs, boolean expand, Zona z) throws SQLException {
		if(expand && rs.getInt("recorridoId") != 0) {
			val rec = new Recorrido();
			rec.id = rs.getInt("recorridoId");
			rec.fechaRetiro = rs.getTimestamp("FechaRetiro").toInstant().atZone(Dates.UTC);
			rec.recicladorId = rs.getLong("RecicladorId");
			rec.reciclador = new Ciudadano(rs.getLong("RecicladorId"), rs.getLong("UsuarioId"));
			rec.puntos = new ArrayList<Retiro>();
			if(rs.getTimestamp("FechaInicio") != null)
				rec.fechaInicio = rs.getTimestamp("FechaInicio").toInstant().atZone(Dates.UTC);
			if(rs.getTimestamp("FechaFin") != null)
				rec.fechaFin = rs.getTimestamp("FechaFin").toInstant().atZone(Dates.UTC);
			
			if(!z.recorridos.contains(rec))
				z.recorridos.add(rec);
		}
	}
}
