package com.module.core.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.AuthenticatedUser;
import com.module.core.models.Session;
import com.module.core.services.TokenService;

public abstract class BaseController {
	
	@Autowired
	TokenService tokenService;
	
	protected boolean isAllow (Session session) {
		AuthenticatedUser user = session.getUser();
		return user!=null;
	}
	
	protected String idUser (Session session) {
		return session.getUser().getId();
	}
	
	protected Session getSession (String token) throws SharknoException {
		return tokenService.readToken(token);
	}
}