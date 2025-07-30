package com.module.core.models.topic;

import java.util.Date;

import com.module.core.models.profile.LiteProfile;

public class TopicResponse {

	private String id;
	private String description;
	private LiteProfile origin;
	private Date creationDate;
	private Date lastUpdate;
	private String topidId;
	private Like like;
	
	public TopicResponse(String id, String description, LiteProfile origin, Date creationDate, Date lastUpdate,
			String topidId, Like like) {
		super();
		this.id = id;
		this.description = description;
		this.origin = origin;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.topidId = topidId;
		this.like = like;
	}

	public TopicResponse() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getTopidId() {
		return topidId;
	}

	public void setTopidId(String topidId) {
		this.topidId = topidId;
	}

	public Like getLike() {
		return like;
	}

	public void setLike(Like like) {
		this.like = like;
	}
	
}
