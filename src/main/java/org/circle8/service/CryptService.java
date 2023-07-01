package org.circle8.service;

import org.mindrot.jbcrypt.BCrypt;

public class CryptService {
	public String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public boolean check(String password, String hashed) {
		return BCrypt.checkpw(password, hashed);
	}
}
