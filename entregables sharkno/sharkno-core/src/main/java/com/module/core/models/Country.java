package com.module.core.models;

public class Country {
	
	private String ISO;
	private String name;
	
	public Country() {
	}

	public Country(String iSO, String name) {
		ISO = iSO;
		this.name = name;
	}

	public String getISO() {
		return ISO;
	}

	public void setISO(String iSO) {
		ISO = iSO;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
