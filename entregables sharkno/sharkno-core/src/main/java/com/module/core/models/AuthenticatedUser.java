package com.module.core.models;

public class AuthenticatedUser {
	
	String id;
	String name;
	String role;
	
	
	
	public AuthenticatedUser() {
	}

	public AuthenticatedUser(String id, String name, String role) {
		super();
		this.id = id;
		this.name = name;
		this.role = role;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	

}
