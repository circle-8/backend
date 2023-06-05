package org.circle8.dao;

import com.google.inject.Singleton;
import org.circle8.entity.User;

@Singleton
public class UserDao {
	public User save(User user) {
		// TODO: implement
		user.id = 1;
		return user;
	}
}
