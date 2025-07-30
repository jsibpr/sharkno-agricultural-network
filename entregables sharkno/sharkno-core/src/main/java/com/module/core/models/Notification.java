package com.module.core.models;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class Notification {

	private String id;
	private String userId;
	private String text;
	@ApiModelProperty(example = "2019-05-29")
	private Date creationDate;
	@ApiModelProperty(example = "2019-05-29")
	private Date readDate;
	private String type;
	private String originId;

	public Notification() {
	}

	public Notification(String id, String userId, String text, Date creationDate, Date readDate, String type, String originId) {
		this.id = id;
		this.userId = userId;
		this.text = text;
		this.creationDate = creationDate;
		this.readDate = readDate;
		this.type = type;
		this.originId = originId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public String getId() {
		return id;
	}

}

