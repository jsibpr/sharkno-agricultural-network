package com.module.core.models.view;

public class TopicResponseForView {

	private String id;
	private String description;
	private String topicId;

	public TopicResponseForView(String id, String description, String topicId) {
		super();
		this.id = id;
		this.description = description;
		this.topicId = topicId;
	}

	public TopicResponseForView() {
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

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}
	
}
