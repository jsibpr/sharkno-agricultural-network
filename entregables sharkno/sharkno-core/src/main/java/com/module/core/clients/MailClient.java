package com.module.core.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.module.core.models.Mail;

@Service
public class MailClient {
	@Value("${mail.url}")
	private String resource;

	@Value("${mail.apiKey}")
	private String apiKey;
	
	public void sendMail(Mail mail) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = getHeaders();
		headers.set("Authorization", apiKey);
		HttpEntity<Mail> jwtEntity = new HttpEntity<>(mail, headers);
		restTemplate.exchange(resource, HttpMethod.PUT, jwtEntity,
				 Mail.class);
	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

}
