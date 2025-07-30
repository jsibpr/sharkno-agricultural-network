package com.module.core.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.module.core.exceptions.AuthenticationException;
import com.module.core.exceptions.ForbiddenException;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.AuthenticatedUser;
import com.module.core.models.User;

@Service
public class AuthClient {

	@Value("${auth.url}")
	private String resource;

	public AuthenticatedUser validateToken(String token) {
		 RestTemplate restTemplate = new RestTemplate();
		 HttpHeaders headers = getHeaders();
		 headers.set("Authorization", token);
		 HttpEntity<String> jwtEntity = new HttpEntity<>(headers);
		 ResponseEntity<AuthenticatedUser> userResponse = restTemplate.exchange(resource + "/validate", HttpMethod.GET, jwtEntity,
				 AuthenticatedUser.class);
		 //userResponse.getStatusCode(); for custom exceptions control
		 return userResponse.getBody();
	}
	
	public String openRegistry(User user) throws SharknoException{
		 RestTemplate restTemplate = new RestTemplate();
		 HttpEntity<User> jwtEntity = new HttpEntity<>(user);
		 ResponseEntity<String> activateTokenResponse = null;
		 try {
			 activateTokenResponse = restTemplate.exchange(resource + "/users/openRegistration", HttpMethod.PUT, jwtEntity,
				 String.class);
		 }catch (HttpClientErrorException e) {
			if(e.getRawStatusCode()==403) {
				throw new ForbiddenException();
			}else {
				throw new AuthenticationException();
			}
		}
		 catch(Exception e) {
			 throw new AuthenticationException();
		 }
		return activateTokenResponse.getBody();
	}
	
	public void activateUser(String activateToken) throws AuthenticationException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = getHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> jwtEntity = new HttpEntity<>(activateToken, headers);
		try {
			restTemplate.exchange(resource + "/users/activateUser", HttpMethod.POST, jwtEntity, void.class);
		}catch (HttpClientErrorException e) {
			throw new AuthenticationException();
		}
		
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

}
