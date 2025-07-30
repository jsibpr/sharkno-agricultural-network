package com.module.files.exceptions;

public class FilesException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public FilesException() {
	}
	
	public FilesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public FilesException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FilesException(String message) {
		super(message);
	}
	
	public FilesException(Throwable cause) {
		super(cause);
	}

}
