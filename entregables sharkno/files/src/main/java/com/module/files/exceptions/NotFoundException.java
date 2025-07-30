package com.module.files.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class NotFoundException extends FilesException{

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super("Entity not found");
	}
}
