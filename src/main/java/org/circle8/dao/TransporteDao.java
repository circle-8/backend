package org.circle8.dao;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.circle8.entity.Transporte;
import org.circle8.exception.PersistenceException;

import com.google.inject.Inject;

public class TransporteDao extends Dao{
	
	private static final String INSERT_SQL = """
			INSERT INTO "Transporte"("PrecioSugerido")
			VALUES (?);
			""";
	
	@Inject
	TransporteDao(DataSource ds) {
		super(ds);
	}
	
	public Transporte save(Transaction t, Transporte transporte) throws PersistenceException {
		try (var insert = t.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
			insert.setBigDecimal(1, transporte.precioSugerido);
			
			int insertions = insert.executeUpdate();
			if (insertions == 0)
				throw new SQLException("Creating the transporte failed, no affected rows");

			try (var rs = insert.getGeneratedKeys()) {
				if (rs.next())
					transporte.id = rs.getLong(1);
				else
					throw new SQLException("Creating the transporte failed, no ID obtained");
			}
		} catch (SQLException e) {
			throw new PersistenceException("error inserting Transaccion", e);
		}

		return transporte;
	}

}
