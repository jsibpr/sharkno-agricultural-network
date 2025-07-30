package com.module.core.models.profile;

public class AboutMe {

	private String id;
	private String shortDescription;
	private String longDescription;
	
	public AboutMe() {
	}

	public AboutMe(String id, String shortDescription, String longDescription) {
		this.id = id;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
