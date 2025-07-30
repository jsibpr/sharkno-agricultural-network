package com.module.core.models;

public class Address {
	
	private Integer id;
	private String city;
	private String country;
	
	public Address(Integer id, String city, String country) {
		this.id = id;
		this.city = city;
		this.country = country;
	}

	public Address() {
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

}
