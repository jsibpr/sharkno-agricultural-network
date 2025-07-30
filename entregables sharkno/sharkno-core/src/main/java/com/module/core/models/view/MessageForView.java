package com.module.core.models.view;

public class MessageForView {
	
	private String text;
	private String destinationId;
	
	public MessageForView() {
	}

	public MessageForView(String text, String destinationId) {
		this.text = text;
		this.destinationId = destinationId;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getDestinationId() {
		return destinationId;
	}
	
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

}
