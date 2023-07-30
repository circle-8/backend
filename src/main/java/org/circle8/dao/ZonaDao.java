package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Organizacion;
import org.circle8.entity.Punto;
import org.circle8.entity.Recorrido;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Zona;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.ZonaFilter;
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
	
	private static final String JOIN_ORGANIZACION = """
			JOIN "Organizacion" AS org on org."ID" = z."OrganizacionId"
			""";
	
	private static final String WHERE_ORGANIZACION = """
			AND z."OrganizacionId" = ?
			""";
	
	private static final String WHERE_ID = """
			AND z."ID" = ?
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
		if(f.organizacion)
			selectFields += SELECT_ORGANIZACION;
		
		var joinFields = "";
		if(f.organizacion)
			joinFields += JOIN_ORGANIZACION;
		
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

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		
		return p;
	}
	
	@NotNull
	private Zona getZona(ResultSet rs,ZonaFilter f) throws SQLException {
		Zona z = null;
		boolean zonaCreada = false;
		while (rs.next()) {
			if (!zonaCreada) {
				z = new Zona();
				z.id = rs.getLong("ID");
				z.nombre = rs.getString("Nombre");
				z.polyline = getPolyline(rs.getString("Polyline"));
				z.organizacionId = rs.getLong("OrganizacionId");
				z.organizacion = buildOrganizacion(rs, f.organizacion);		
				z.tipoResiduo = new ArrayList<TipoResiduo>();
				z.recorridos = new ArrayList<Recorrido>();
				zonaCreada = true;
			}
			if (rs.getInt("TipoResiduoId") != 0)
				z.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("tipoResiduoNombre")));

		}
		return z;
	}
	
	private List<Zona> getList(ResultSet rs,ZonaFilter f) throws SQLException {
		var mapZonas = new HashMap<Long, Zona>();
		while (rs.next()) {
			val id = rs.getLong("ID");
			Zona z = mapZonas.get(id);
			if (z == null) {
				z = new Zona();
				z.id = rs.getLong("ID");
				z.nombre = rs.getString("Nombre");
				z.polyline = getPolyline(rs.getString("Polyline"));
				z.organizacionId = rs.getLong("OrganizacionId");
				z.organizacion = buildOrganizacion(rs, f.organizacion);
				z.tipoResiduo = new ArrayList<TipoResiduo>();
				z.recorridos = new ArrayList<Recorrido>();
				mapZonas.put(id, z);
			}
			if (rs.getInt("TipoResiduoId") != 0)
				z.tipoResiduo.add(new TipoResiduo(rs.getInt("TipoResiduoId"), rs.getString("tipoResiduoNombre")));
		}

		return mapZonas.values().stream().toList();
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

}
