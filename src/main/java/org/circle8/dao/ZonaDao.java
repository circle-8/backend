package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Organizacion;
import org.circle8.entity.Punto;
import org.circle8.entity.TipoResiduo;
import org.circle8.entity.Zona;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.ZonaFilter;
import org.jetbrains.annotations.NotNull;

import com.google.inject.Inject;

import lombok.val;

public class ZonaDao extends Dao {
	private static final String SELECT_FMT = """
			SELECT
			       %s
			  FROM "Zona" AS z
			    %s
			 WHERE 1 = 1
			""";
	
	private static final String SELECT_SIMPLE = """
			z."ID", z."OrganizacionId", z."Polyline", z."Nombre"
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
				if ( !rs.next() )
					return Optional.empty();
				return Optional.of(buildZona(rs));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting solicitud", e);
		}
	}
	
	private PreparedStatement createSelect(
		Transaction t,
		ZonaFilter f
	) throws PersistenceException, SQLException {
		var selectFields = SELECT_SIMPLE;
		var joinFields = "";
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
	private Zona buildZona(ResultSet rs) throws SQLException {
		val z = new Zona();
		z.id = rs.getLong("ID");
		z.nombre = rs.getString("Nombre");
		z.polyline = new ArrayList<Punto>();
		z.organizacionId = rs.getLong("OrganizacionId");
		z.organizacion = Organizacion.builder()
				.id(rs.getLong("OrganizacionId"))
				.build();
		z.tipoResiduo = new ArrayList<TipoResiduo>();		
		return z;
	}

}
