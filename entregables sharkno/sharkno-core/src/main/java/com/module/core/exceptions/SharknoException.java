package com.module.core.exceptions;

public class SharknoException extends Exception{

	private static final long serialVersionUID = 1L;

	public SharknoException() {
	}

	public SharknoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SharknoException(String message, Throwable cause) {
		super(message, cause);
	}

	public SharknoException(String message) {
		super(message);
	}

	public SharknoException(Throwable cause) {
		super(cause);
	}
	
	

}
