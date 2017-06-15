package com.aquariumpain.dialect.web.daos;

public class User {
	public long id;
	public String username;
	public String email;
	public String githubId;
	public String token;
	public boolean isTrainer;
	public boolean isAdmin;
	public boolean isBanned;
	
	public String getToken() {
		return token;
	}
}
