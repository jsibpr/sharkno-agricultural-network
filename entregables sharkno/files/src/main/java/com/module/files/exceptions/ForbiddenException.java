package com.module.files.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Access forbidden")
public class ForbiddenException extends FilesException{

	private static final long serialVersionUID = 1L;
	
	public ForbiddenException() {
		super("Access forbidden");
	}

}