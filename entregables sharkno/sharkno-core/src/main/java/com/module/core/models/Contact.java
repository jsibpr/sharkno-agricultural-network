package com.module.core.models;

import java.time.LocalDate;

import io.swagger.annotations.ApiModelProperty;

public class Contact {
	
	public enum Status {
		PENDING,
		REJECTED,
		CONFIRMED
	}
	
	private String id;
	@ApiModelProperty(example = "2019-05-29")
	private LocalDate creationDate;
	private String origin;
	private String destination;
	private Status status;
		
	public Contact() {
	}
	
	public Contact(String id, LocalDate creationDate, String origin, String destination, Status status) {
		this.id = id;
		this.creationDate = creationDate;
		this.origin = origin;
		this.destination = destination;
		this.status = status;
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public LocalDate getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}


	public String getOrigin() {
		return origin;
	}


	public void setOrigin(String origin) {
		this.origin = origin;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}	
}
