package com.module.mail.exceptions;

public class MailException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public MailException() {
	}
	
	public MailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public MailException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MailException(String message) {
		super(message);
	}
	
	public MailException(Throwable cause) {
		super(cause);
	}

}
