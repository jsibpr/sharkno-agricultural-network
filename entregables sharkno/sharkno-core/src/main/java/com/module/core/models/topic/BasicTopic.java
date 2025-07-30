package com.module.core.models.topic;

import java.util.Date;
import java.util.List;

import com.module.core.models.Entity;
import com.module.core.models.profile.LiteProfile;

public class BasicTopic {

	private String id;
	private String title;
	private String description;
	private Entity category;
	private List<Entity> skills;
	private LiteProfile origin;
	private Date creationDate;
	private Date lastUpdate;
	private int responsesQty;
	
	public BasicTopic(String id, String title, String description, Entity category, List<Entity> skills,
			LiteProfile origin, Date creationDate, Date lastUpdate, int responsesQty) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.category = category;
		this.skills = skills;
		this.origin = origin;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.responsesQty = responsesQty;
	}

	public BasicTopic() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Entity getCategory() {
		return category;
	}

	public void setCategory(Entity category) {
		this.category = category;
	}

	public List<Entity> getSkills() {
		return skills;
	}

	public void setSkills(List<Entity> skills) {
		this.skills = skills;
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

	public int getResponsesQty() {
		return responsesQty;
	}

	public void setResponsesQty(int responsesQty) {
		this.responsesQty = responsesQty;
	}
	
}
