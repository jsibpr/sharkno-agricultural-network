package com.module.core.models;

public class Mail {
	
	private String id;
	private String subject;
	private String destinationMail;
	private String message;

	public Mail() {
	}

	public Mail(String id, String subject, String destinationMail, String message) {
		super();
		this.id = id;
		this.subject = subject;
		this.destinationMail = destinationMail;
		this.message = message;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getDestinationMail() {
		return destinationMail;
	}


	public void setDestinationMail(String destinationMail) {
		this.destinationMail = destinationMail;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
}
