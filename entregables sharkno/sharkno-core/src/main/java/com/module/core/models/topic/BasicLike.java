package com.module.core.models.topic;

import java.util.Date;

import com.module.core.models.profile.LiteProfile;

public class BasicLike {

	private LiteProfile origin;
	private String destination;
	private String type;
	private Date creationDate;
	
	public BasicLike(LiteProfile origin, String destination, String type, Date creationDate) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.type = type;
		this.creationDate = creationDate;
	}

	public BasicLike() {
		super();
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
