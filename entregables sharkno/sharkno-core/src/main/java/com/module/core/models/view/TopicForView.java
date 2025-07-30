package com.module.core.models.view;

import java.util.List;

public class TopicForView {
	
	private String id;
	private String title;
	private List<String> skillsIds;
	private String description;
	private String categoryId;
	
	public TopicForView(String id, String title, List<String> skillsIds, String description, String categoryId) {
		super();
		this.id = id;
		this.title = title;
		this.skillsIds = skillsIds;
		this.description = description;
		this.categoryId = categoryId;
	}

	public TopicForView() {
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
	
	public List<String> getSkillsIds() {
		return skillsIds;
	}

	public void setSkillsIds(List<String> skillsIds) {
		this.skillsIds = skillsIds;
	}
	 
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
}
