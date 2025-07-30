package com.module.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.module.core.clients.AuthClient;
import com.module.core.exceptions.AuthenticationException;
import com.module.core.models.AuthenticatedUser;
import com.module.core.models.Session;


@Service
public class TokenService {
	
	@Autowired
	AuthClient authClient;

	public Session readToken(String token) throws AuthenticationException {
		if (StringUtils.isEmpty(token)){
			throw new AuthenticationException();
		}
		try {
			Session session = new Session();
			session.setToken(token);
			AuthenticatedUser user = authClient.validateToken(token);
			session.setUser(user);
			return session;
		} catch (Exception e) {
			throw new AuthenticationException();
		}
	}
}