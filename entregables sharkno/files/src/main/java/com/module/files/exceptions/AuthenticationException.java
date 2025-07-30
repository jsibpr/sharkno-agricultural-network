package com.module.files.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User unauthorized")
public class AuthenticationException extends FilesException{
	
	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
		super("User unauthorized");
	}

}
