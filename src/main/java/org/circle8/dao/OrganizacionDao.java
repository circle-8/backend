package org.circle8.dao;

import com.google.inject.Inject;
import org.circle8.entity.Organizacion;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

public class OrganizacionDao extends Dao {
	@Inject public OrganizacionDao(DataSource ds) { super(ds); }

	public Organizacion save(Transaction t, Organizacion o) throws PersistenceException {
		var insertSQL = "INSERT INTO \"Organizacion\"(\"RazonSocial\", \"UsuarioId\") VALUES(?, ?)";
		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, o.razonSocial);
			insert.setLong(2, o.usuarioId);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the organizacion failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if ( rs.next() ) o.id = rs.getLong(1);
				else throw new SQLException("Creating the transportista failed, no ID obtained");
			}

			return o;
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Organizacion_UsuarioId_key") )
				throw new DuplicatedEntry("organizacion already exists", e);
			else
				throw new PersistenceException("error inserting organizacion", e);
		}
	}
}
