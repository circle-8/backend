package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class UserDao extends Dao {

	@Inject
	public UserDao(DataSource ds) {
		super(ds);
	}

	public User save(Transaction t, User user) throws PersistenceException {
		var insertSQL = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario")
			  VALUES (?, ?, ?, ?)""";

		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, user.nombre);
			insert.setString(2, user.username);
			insert.setString(3, user.hashedPassword);
			insert.setString(4, user.tipo.name());

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the user failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					user.id = rs.getInt(1);
				else
					throw new SQLException("Creating the user failed, no ID obtained");
			}
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Usuario_Username_key") )
				throw new DuplicatedEntry("username already exists", e);
			else
				throw new PersistenceException("error inserting user", e);
		}

		return user;
	}
}
