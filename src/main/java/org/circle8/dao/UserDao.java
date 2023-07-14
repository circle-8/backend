package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dto.TipoUsuario;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Singleton
public class UserDao extends Dao {

	@Inject
	public UserDao(DataSource ds) {
		super(ds);
	}

	public User save(Transaction t, User user) throws PersistenceException {
		var insertSQL = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario", "Email")
			  VALUES (?, ?, ?, ?, ?)""";

		try ( var insert = t.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, user.nombre);
			insert.setString(2, user.username);
			insert.setString(3, user.hashedPassword);
			insert.setString(4, user.tipo.name());
			insert.setString(5, user.email);

			int insertions = insert.executeUpdate();
			if ( insertions == 0 )
				throw new SQLException("Creating the user failed, no affected rows");

			try ( var rs = insert.getGeneratedKeys() ) {
				if (rs.next())
					user.id = rs.getLong(1);
				else
					throw new SQLException("Creating the user failed, no ID obtained");
			}
		} catch ( SQLException e ) {
			if ( e.getMessage().contains("Usuario_Username_key") )
				throw new DuplicatedEntry("username already exists", e);
			else if ( e.getMessage().contains("Usuario_Email_key") )
				throw new DuplicatedEntry("email already exists", e);
			else
				throw new PersistenceException("error inserting user", e);
		}

		return user;
	}

	public Optional<User> get(String username) throws PersistenceException {
		var selectSQL = """
   SELECT "ID", "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email"
     FROM "Usuario"
    WHERE "Username" = ?""";

		try ( var t = open(true); var select = t.prepareStatement(selectSQL) ) {
			select.setString(1, username);

			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();

				return Optional.of(User.builder()
					.id(rs.getLong("Id"))
					.username(rs.getString("Username"))
					.hashedPassword(rs.getString("Password"))
					.nombre(rs.getString("NombreApellido"))
					.email(rs.getString("Email"))
					.tipo(TipoUsuario.valueOf(rs.getString("TipoUsuario")))
					.build());
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting user", e);
		}
	}
}
