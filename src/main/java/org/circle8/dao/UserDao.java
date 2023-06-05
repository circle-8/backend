package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;

@Singleton
public class UserDao {
	private final DataSource ds;

	@Inject
	public UserDao(DataSource ds) {
		this.ds = ds;
	}

	public User save(User user) {
		var insertSQL = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario")
			  VALUES (?, ?, ?, ?)""";

		try ( var conn = ds.getConnection() ) {
			try ( var insert = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS) ) {
				conn.setAutoCommit(false);
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
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch ( SQLException e ) {
			// TODO log and exception
			e.printStackTrace();
		}

		return user;
	}
}
