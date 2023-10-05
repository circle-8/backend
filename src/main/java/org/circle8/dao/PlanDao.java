package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.entity.Plan;
import org.circle8.entity.Recorrido;
import org.circle8.exception.PersistenceException;
import org.circle8.expand.RecorridoExpand;
import org.circle8.filter.RecorridoFilter;

import com.google.inject.Inject;

import lombok.val;

public class PlanDao extends Dao{

	private static final String SELECT = """
		SELECT p."ID", p."Nombre", p."Precio", p."MesesRenovacion", p."CantUsuarios"
		  FROM "Plan" p
		 WHERE 1=1
		""";

	private static final String WHERE_ID = "AND p.\"ID\" = ?\n";

	@Inject
	PlanDao(DataSource ds) {
		super(ds);
	}

	public Optional<Plan> get(
		Transaction t,
		long id
	) throws PersistenceException {
		try (
			val select = createSelect(t, id);
			val rs = select.executeQuery()
		) {
			if ( !rs.next() ) return Optional.empty();

			return Optional.of(buildPlan(rs));
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting plan", e);
		}
	}

	Plan buildPlan(ResultSet rs) throws SQLException {
		return Plan.builder()
						 .id(rs.getLong("ID"))
						 .cantUsuarios(rs.getInt("CantUsuarios"))
						 .nombre(rs.getString("Nombre"))
						 .precio(rs.getBigDecimal("Precio"))
						 .mesesRenovacion(rs.getInt("MesesRenovacion"))
						 .build();
	}

	private PreparedStatement createSelect(Transaction t, long id) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT);
		b.append(WHERE_ID);

		var p = t.prepareStatement(b.toString());
		p.setLong(1, id);

		return p;
	}
}
