package com.module.files.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE, reason = "Error while connecting to file repository")
public class RepositoryException extends FilesException {

	private static final long serialVersionUID = 1L;
	
	public RepositoryException () {
		super("Error while connecting to file repository");
	}
	
	public RepositoryException (Throwable cause) {
		super(cause);
	}
}
