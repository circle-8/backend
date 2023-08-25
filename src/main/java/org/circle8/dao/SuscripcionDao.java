package org.circle8.dao;

import com.google.inject.Inject;
import org.circle8.entity.Suscripcion;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

public class SuscripcionDao extends Dao {
	private static final String INSERT = """
		INSERT INTO public."Suscripcion"("UltimaRenovacion", "ProximaRenovacion", "PlanId")
		VALUES (?, ?, ?);
		""";

	@Inject SuscripcionDao(DataSource ds) { super(ds); }

	public Suscripcion save(Transaction t, Suscripcion s) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setDate(1, Date.valueOf(s.ultimaRenovacion));
			insert.setDate(2, Date.valueOf(s.proximaRenovacion));
			insert.setLong(3, s.plan.id);
			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the suscripcion failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() ) s.id = rs.getLong(1);
				else throw new SQLException("Creating the suscripcion failed, no ID obtained");
			}

			return s;
		} catch ( SQLException e ) {
			throw new PersistenceException("error inserting suscripcion", e);
		}
	}
}
