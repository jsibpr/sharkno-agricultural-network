package com.module.core.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User unauthorized")
public class AuthenticationException extends SharknoException{

	private static final long serialVersionUID = 1L;

	public AuthenticationException() {
		super("User unauthorized");
	}
	

}
