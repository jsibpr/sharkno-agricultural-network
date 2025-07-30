package com.module.core.models.view;

public class RecommendationForView {
	
	private String destinationId;
	private String description;
	
	public RecommendationForView() {
	}

	public RecommendationForView(String destinationId, String description) {
		this.destinationId = destinationId;
		this.description = description;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
}

	
