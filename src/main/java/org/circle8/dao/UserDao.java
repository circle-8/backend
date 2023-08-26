package org.circle8.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.circle8.dto.TipoUsuario;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Singleton
public class UserDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario", "Email", "SuscripcionId")
			  VALUES (?, ?, ?, ?, ?, ?)
			  """;
	
	private static final String UPDATE = """
			UPDATE "Usuario"
			SET "NombreApellido"=?, "Username"=?, "Password"=?, "TipoUsuario"=?, "Email"=?
			WHERE "ID"=?;
			""";

	private static final String SELECT_GET = """
		   SELECT u."ID", "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email", c."ID" AS CiudadanoId , r."ID" AS RecicladorId, r."OrganizacionId", r."ZonaId"
		     FROM "Usuario" u
		LEFT JOIN "Ciudadano" c on c."UsuarioId" = u."ID"
		LEFT JOIN "RecicladorUrbano" r on r."UsuarioId" = u."ID"
		    WHERE "Username" = ?
		""";

	@Inject
	public UserDao(DataSource ds) {
		super(ds);
	}

	public User save(Transaction t, User user) throws PersistenceException {
		try ( var insert = t.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS) ) {
			insert.setString(1, user.nombre);
			insert.setString(2, user.username);
			insert.setString(3, user.hashedPassword);
			insert.setString(4, user.tipo.name());
			insert.setString(5, user.email);
			insert.setObject(6, user.suscripcion.id != 0 ? user.suscripcion.id : null);

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
	
	public User update(Transaction t, User user) throws PersistenceException, NotFoundException {
		try ( var put = t.prepareStatement(UPDATE) ) {
			put.setString(1, user.nombre);
			put.setString(2, user.username);
			put.setString(3, user.hashedPassword);
			put.setString(4, user.tipo.name());
			put.setString(5, user.email);
			put.setLong(6, user.id);

			int puts = put.executeUpdate();
			if ( puts == 0 )
				throw new NotFoundException("No existe el usuario con id " + user.id);
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
		try ( var t = open(true); var select = t.prepareStatement(SELECT_GET) ) {
			select.setString(1, username);
			try ( var rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();
				return Optional.of(buildUser(rs));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting user", e);
		}
	}

	private User buildUser(ResultSet rs) throws SQLException {
		var u = new User();
		u.id = rs.getLong("Id");
		u.username = rs.getString("Username");
		u.hashedPassword = rs.getString("Password");
		u.nombre = rs.getString("NombreApellido");
		u.tipo = TipoUsuario.valueOf(rs.getString("TipoUsuario"));
		if(TipoUsuario.CIUDADANO.equals(u.tipo))
			u.ciudadanoId = rs.getLong("CiudadanoId");
		else if(TipoUsuario.RECICLADOR_URBANO.equals(u.tipo)) {
			u.recicladorUrbanoId = rs.getLong("RecicladorId");
			u.organizacionId = rs.getLong("OrganizacionId");
			u.zonaId = rs.getLong("ZonaId") != 0 ?
					rs.getLong("ZonaId") : null;
		}
		return u;
	}
}
