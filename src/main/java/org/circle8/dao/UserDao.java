package org.circle8.dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.val;
import org.circle8.dto.TipoUsuario;
import org.circle8.entity.RecicladorUrbano;
import org.circle8.entity.User;
import org.circle8.exception.DuplicatedEntry;
import org.circle8.exception.NotFoundException;
import org.circle8.exception.PersistenceException;
import org.circle8.filter.UserFilter;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserDao extends Dao {

	private static final String INSERT = """
			INSERT INTO "Usuario"(
			  "NombreApellido", "Username", "Password", "TipoUsuario", "Email", "SuscripcionId")
			  VALUES (?, ?, ?, ?, ?, ?)
			  """;

	private static final String SELECT_GET = """
		   SELECT u."ID", "NombreApellido", "Username", "Password", "SuscripcionId", "TipoUsuario",
		          "Email", c."ID" AS CiudadanoId,
		       -- Reciclador Urbano
		          r."ID" AS RecicladorId, r."OrganizacionId", r."ZonaId",
		          r."FechaNacimiento", r."DNI", r."Domicilio", r."Telefono",
		       -- Organizacion
		          o."ID" AS "OOrganizacionId",
		       -- Transportista
		          t."ID" AS TransportistaId
		     FROM "Usuario" u
		LEFT JOIN "Ciudadano" c ON c."UsuarioId" = u."ID"
		LEFT JOIN "RecicladorUrbano" r ON r."UsuarioId" = u."ID"
		LEFT JOIN "Transportista" t ON t."UsuarioId" = u."ID"
		LEFT JOIN "Organizacion" o ON o."UsuarioId" = u."ID"
		    WHERE 1 = 1
		""";

	private static final String WHERE_ID = "AND u.\"ID\" = ?\n";
	private static final String WHERE_USER_NAME = "AND u.\"Username\" = ?\n";
	private static final String WHERE_ORGANIZACION_ID = "AND (r.\"OrganizacionId\" = ? OR o.\"ID\" = ?)\n";
	private static final String WHERE_TIPO_USUARIO = "AND u.\"TipoUsuario\" = ?\n";

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

	public Optional<User> get(String username, Long id) throws PersistenceException {
		val f = UserFilter.builder().id(id).username(username).build();
		try (val t = open(true); val select = createSelect(t, f) ) {
			try ( val rs = select.executeQuery() ) {
				if ( !rs.next() )
					return Optional.empty();
				return Optional.of(buildUser(rs));
			}
		} catch ( SQLException e ) {
			throw new PersistenceException("error getting user", e);
		}
	}

	private PreparedStatement createSelect(Transaction t, UserFilter f) throws PersistenceException, SQLException {
		var b = new StringBuilder(SELECT_GET);
		List<Object> parameters = new ArrayList<>();

		appendCondition(f.username, WHERE_USER_NAME, b, parameters);
		appendCondition(f.id, WHERE_ID, b, parameters);
		appendCondition(f.tipoUsuario, WHERE_TIPO_USUARIO, b, parameters);

		if ( f.organizacionId != null ) {
			b.append(WHERE_ORGANIZACION_ID);
			parameters.add(f.organizacionId);
			parameters.add(f.organizacionId);
		}

		var p = t.prepareStatement(b.toString());
		for (int i = 0; i < parameters.size(); i++)
			p.setObject(i + 1, parameters.get(i));

		return p;
	}

	private User buildUser(ResultSet rs) throws SQLException {
		var u = new User();
		u.id = rs.getLong("Id");
		u.username = rs.getString("Username");
		u.hashedPassword = rs.getString("Password");
		u.nombre = rs.getString("NombreApellido");
		u.tipo = TipoUsuario.valueOf(rs.getString("TipoUsuario"));
		u.email = rs.getString("Email");

		switch ( u.tipo ) {
			case CIUDADANO -> u.ciudadanoId = rs.getLong("CiudadanoId");
			case TRANSPORTISTA -> u.transportistaId = rs.getLong("TransportistaId");
			case ORGANIZACION -> u.organizacionId = rs.getLong("OOrganizacionId");
			case RECICLADOR_URBANO -> {
				u.recicladorUrbanoId = rs.getLong("RecicladorId");
				u.organizacionId = rs.getLong("OrganizacionId");
				u.zonaId = rs.getLong("ZonaId") != 0
					? rs.getLong("ZonaId")
					: null;
				var date = rs.getDate("FechaNacimiento");
				u.reciclador = new RecicladorUrbano(
					rs.getLong("RecicladorId"),
					rs.getLong("ID"),
					rs.getLong("OrganizacionId"),
					u.zonaId,
					date != null ? date.toLocalDate() : null,
					rs.getString("DNI"),
					rs.getString("Domicilio"),
					rs.getString("Telefono")
				);
			}
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

	public List<User> list(UserFilter f) throws PersistenceException {
		try (
			val t = open();
			val select = createSelect(t, f);
			val rs = select.executeQuery()
		) {
			val l = new ArrayList<User>();
			while ( rs.next() ) l.add(buildUser(rs));
			return l;
		} catch (SQLException e) {
			throw new PersistenceException("error getting users", e);
		}
	}
}
