package com.module.core.models.topic;

import java.util.Date;
import java.util.List;

import com.module.core.models.Entity;
import com.module.core.models.profile.LiteProfile;

public class Topic extends BasicTopic{

	private List<TopicResponse> responses;

	public Topic(String id, String title, String description, Entity category, List<Entity> skills, LiteProfile origin,
			Date creationDate, Date lastUpdate, int responsesQty, List<TopicResponse> responses) {
		super(id, title, description, category, skills, origin, creationDate, lastUpdate, responsesQty);
		this.responses = responses;
	}

	public Topic() {
		super();
	}

	public List<TopicResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<TopicResponse> responses) {
		this.responses = responses;
	}

}
