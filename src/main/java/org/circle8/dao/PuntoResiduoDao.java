package org.circle8.dao;

import com.google.inject.Inject;
import lombok.val;
import org.circle8.dto.TipoUsuario;
import org.circle8.entity.PuntoResiduo;
import org.circle8.entity.User;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.PuntoResiduoExpand;
import org.circle8.filter.PuntoResiduoFilter;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PuntoResiduoDao extends Dao {
	@Inject
	public PuntoResiduoDao(DataSource ds) { super(ds); }

	public List<PuntoResiduo> list(PuntoResiduoFilter f, PuntoResiduoExpand x) throws PersistenceException {
		try ( val t = open(true); val select = createSelect(t, f, x) ) {
			try ( val rs = select.executeQuery() ) {
				val l = new ArrayList<PuntoResiduo>();
				while ( rs.next() ) {
					val u = new User();
					u.id = rs.getLong("UsuarioId");
					if ( x.ciudadano ) {
						u.username = rs.getString("Username");
						u.nombre = rs.getString("NombreApellido");
						u.email = rs.getString("Email");
						u.tipo = TipoUsuario.valueOf(rs.getString("TipoUsuario"));
					}
					l.add(new PuntoResiduo(
						rs.getLong("ID"),
						rs.getDouble("Latitud"),
						rs.getDouble("Longitud"),
						rs.getLong("CiudadanoId"),
						u
					));
				}
				return l;
			}
		} catch ( SQLException e) {
			throw new PersistenceException("error getting PuntoResiduo", e);
		}
	}

	private PreparedStatement createSelect(
		Transaction t,
		PuntoResiduoFilter f,
		PuntoResiduoExpand x
	) throws PersistenceException, SQLException {
		val selectFmt = """
 SELECT %s
   FROM "PuntoResiduo" AS pr
   %s
  WHERE 1=1
  """;

		// TODO: constantes
		final String select;
		if ( x.ciudadano ) {
			select = "pr.\"ID\", \"Latitud\", \"Longitud\", \"CiudadanoId\", c.\"UsuarioId\"," +
				"u.\"Username\", u.\"NombreApellido\", u.\"Email\", u.\"TipoUsuario\"";
		} else {
			select = "pr.\"ID\", \"Latitud\", \"Longitud\", \"CiudadanoId\", c.\"UsuarioId\"";
		}

		final StringBuilder joinB = new StringBuilder("""
JOIN "Ciudadano" AS c ON c."ID" = pr."CiudadanoId"
""");
		if ( f.hasTipo() ) {
			joinB.append("""
JOIN "Residuo" AS r ON r."PuntoResiduoId" = pr."ID"
JOIN "TipoResiduo" AS tr on tr."ID" = r."TipoResiduoId"
""");
		}
		if ( x.ciudadano ) {
			joinB.append("""
JOIN "Usuario" AS u ON u."ID" = c."UsuarioId"
""");
		}

		val join = joinB.toString();
		val sql = String.format(selectFmt, select, join);

		val b = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		if ( f.hasArea() ) {
			// TODO: constantes
			b.append("AND pr.\"Latitud\" BETWEEN ? AND ?\n")
				.append("AND pr.\"Longitud\" BETWEEN ? AND ?\n");
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		if ( f.hasTipo() ) {
			// TODO: constantes
			String marks = f.tipoResiduos.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			b.append("AND tr.\"Nombre\" IN (").append(marks).append(")\n")
				.append("AND r.\"FechaRetiro\" IS NULL\n")
				.append("AND r.\"RecorridoId\" IS NULL\n")
				.append("AND r.\"TransaccionId\" IS NULL\n")
			;

			parameters.addAll(f.tipoResiduos);
		}

		val p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++) {
			p.setObject(i+1, parameters.get(i));
		}

		return p;
	}
}
