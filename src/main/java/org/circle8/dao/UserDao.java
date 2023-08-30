package org.circle8.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.circle8.dto.TipoUsuario;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import lombok.val;

@Singleton
public class UserDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario", "Email", "SuscripcionId")
			  VALUES (?, ?, ?, ?, ?, ?)
			  """;

	private static final String SELECT_GET = """
		   SELECT u."ID", "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email", c."ID" AS CiudadanoId , r."ID" AS RecicladorId, r."OrganizacionId", r."ZonaId"
		     FROM "Usuario" u
		LEFT JOIN "Ciudadano" c on c."UsuarioId" = u."ID"
		LEFT JOIN "RecicladorUrbano" r on r."UsuarioId" = u."ID"
		    WHERE "Username" = ?
		""";
	
	private static final String SELECT_GET_BY_ID = """
			   SELECT u."ID", "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario", "Email", c."ID" AS CiudadanoId , r."ID" AS RecicladorId, r."OrganizacionId", r."ZonaId"
			     FROM "Usuario" u
			LEFT JOIN "Ciudadano" c on c."UsuarioId" = u."ID"
			LEFT JOIN "RecicladorUrbano" r on r."UsuarioId" = u."ID"
			    WHERE u."ID" = ?
			""";
	
	private static final String UPDATE = """
			UPDATE "Usuario"
			SET %s
			WHERE "ID"=?;
			""";
	
	private static final String SET_NOMBRE = """
			"NombreApellido"=?
			""";
	
	private static final String SET_USERNAME = """
			"Username"=?
			""";
	
	private static final String SET_TIPO_USUARIO = """
			"TipoUsuario"=?
			""";
	
	private static final String SET_EMAIL = """
			"Email"=?
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
		try ( var put = createUpdate(t, user) ) {

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
	
	public Optional<User> getById(Long id) throws PersistenceException {
		try ( var t = open(true); var select = t.prepareStatement(SELECT_GET_BY_ID) ) {
			select.setLong(1, id);
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
	
	private PreparedStatement createUpdate(Transaction t, User u) throws PersistenceException, SQLException {
		val set = new ArrayList<String>();
		List<Object> parameters = new ArrayList<>();
		
		if(!Strings.isNullOrEmpty(u.nombre)) {
			set.add(SET_NOMBRE);
			parameters.add(u.nombre);
		}
		
		if(!Strings.isNullOrEmpty(u.username)) {
			set.add(SET_USERNAME);
			parameters.add(u.username);
		}
		
		if(u.tipo != null) {
			set.add(SET_TIPO_USUARIO);
			parameters.add(u.tipo.name());
		}		
		
		if(!Strings.isNullOrEmpty(u.email)) {
			set.add(SET_EMAIL);
			parameters.add(u.email);
		}
		
		parameters.add(u.id);
		
		val sets = String.join(", ", set);
		val sql = String.format(UPDATE, sets);
		var p = t.prepareStatement(sql);
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));
		return p;
	}
}
