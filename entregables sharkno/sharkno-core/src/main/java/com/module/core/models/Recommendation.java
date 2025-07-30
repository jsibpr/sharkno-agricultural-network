package com.module.core.models;

import java.util.Date;

import com.module.core.models.profile.LiteProfile;

public class Recommendation {

	private String id;
	private LiteProfile origin;
	private String destinationId;
	private String description;
	private Date creationDate;

	public Recommendation(String id, LiteProfile origin, String destinationId, String description, Date creationDate) {
		this.id = id;
		this.origin = origin;
		this.destinationId = destinationId;
		this.description = description;
		this.creationDate = creationDate;
	}

	public Recommendation() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	
}
