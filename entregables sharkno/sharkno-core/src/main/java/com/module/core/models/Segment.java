package com.module.core.models;

import java.time.LocalDate;

import io.swagger.annotations.ApiModelProperty;

public class Segment {
	
	public enum Type {
		EXPERIENCE,
		EDUCATION
	}
	
	private String id;
	private String position;
	private String company;
	@ApiModelProperty(example = "2019-05-29")
	private LocalDate startDate;
	@ApiModelProperty(example = "2019-05-29")
	private LocalDate endDate;
	private Address address;
	private String description;
	private Type type;
	
	public Segment(String id, String position, String company, LocalDate startDate, LocalDate endDate, Address address,
			String description, Type type) {
		this.id = id;
		this.position = position;
		this.company = company;
		this.startDate = startDate;
		this.endDate = endDate;
		this.address = address;
		this.description = description;
		this.type = type;
	}

	public Segment() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
