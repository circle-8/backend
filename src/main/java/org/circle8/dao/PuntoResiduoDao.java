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
	private static final String SELECT_FMT = """
		SELECT %s
		  FROM "PuntoResiduo" AS pr
		  JOIN "Ciudadano" AS c ON c."ID" = pr."CiudadanoId"
		    %s
		 WHERE 1=1
		""";
	private static final String SELECT_SIMPLE = "pr.\"ID\", \"Latitud\", \"Longitud\", \"CiudadanoId\", c.\"UsuarioId\"";
	private static final String SELECT_CIUDADANO = SELECT_SIMPLE + ", u.\"Username\", u.\"NombreApellido\", u.\"Email\", u.\"TipoUsuario\"\n";
	private static final String JOIN_TIPO = """
		JOIN "Residuo" AS r ON r."PuntoResiduoId" = pr."ID"
		JOIN "TipoResiduo" AS tr on tr."ID" = r."TipoResiduoId"
		""";
	private static final String JOIN_CIUDADANO = "JOIN \"Usuario\" AS u ON u.\"ID\" = c.\"UsuarioId\"\n";
	private static final String WHERE_AREA = """
		AND pr."Latitud" BETWEEN ? AND ?
		AND pr."Longitud" BETWEEN ? AND ?
		""";
	private static final String WHERE_TIPO = """
		AND tr."Nombre" IN ( %s )
		AND r."RecorridoId" IS NULL
		AND r."TransaccionId" IS NULL
		""";

	@Inject
	public PuntoResiduoDao(DataSource ds) { super(ds); }

	public List<PuntoResiduo> list(PuntoResiduoFilter f, PuntoResiduoExpand x) throws PersistenceException {
		try ( val t = open(true); val select = createSelect(t, f, x); val rs = select.executeQuery() ) {
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
		} catch ( SQLException e) {
			throw new PersistenceException("error getting PuntoResiduo", e);
		}
	}

	private PreparedStatement createSelect(
		Transaction t,
		PuntoResiduoFilter f,
		PuntoResiduoExpand x
	) throws PersistenceException, SQLException {
		final String select = x.ciudadano ? SELECT_CIUDADANO : SELECT_SIMPLE;

		val joinB = new StringBuilder();
		if ( f.hasTipo() ) joinB.append(JOIN_TIPO);
		if ( x.ciudadano ) joinB.append(JOIN_CIUDADANO);

		val join = joinB.toString();
		val sql = String.format(SELECT_FMT, select, join);

		val conditions = new StringBuilder(sql);
		List<Object> parameters = new ArrayList<>();
		if ( f.hasArea() ) {
			conditions.append(WHERE_AREA);
			parameters.add(f.latitud - f.radio);
			parameters.add(f.latitud + f.radio);
			parameters.add(f.longitud - f.radio);
			parameters.add(f.longitud + f.radio);
		}

		if ( f.hasTipo() ) {
			val marks = f.tipoResiduos.stream()
				.map(tr -> "?")
				.collect(Collectors.joining(","));

			conditions.append(String.format(WHERE_TIPO, marks));
			parameters.addAll(f.tipoResiduos);
		}

		val p = t.prepareStatement(conditions.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i+1, parameters.get(i));

		return p;
	}
}
