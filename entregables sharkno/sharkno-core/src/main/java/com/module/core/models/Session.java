package com.module.core.models;

public class Session {
	
	private String token;
	private AuthenticatedUser user;
	
	public Session(String token, AuthenticatedUser user) {
		this.token = token;
		this.user = user;
	}
	
	public Session() {
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public AuthenticatedUser getUser() {
		return user;
	}
	
	public void setUser(AuthenticatedUser user) {
		this.user = user;
	}

}
