package com.module.core.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.module.core.models.Notification;

@Service
public class NotificationClient {
	
	private static final String AUTH = "Authorization";

	@Value("${notification.url}")
	private String resource;
	
	@Value("${notification.apiKey}")
 	private String apiKey;

	public void createNotification(Notification notification) {
		 RestTemplate restTemplate = new RestTemplate();
		 HttpHeaders headers = new HttpHeaders();
		 headers.add(AUTH, apiKey);
		 HttpEntity<Notification> request = new HttpEntity< >(notification, headers);
		 restTemplate.put(resource, request);
	}
	
	public void updateNotification (Notification notification) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTH, apiKey);
		HttpEntity<Notification> request = new HttpEntity< >(notification, headers);
		restTemplate.postForEntity(resource, request, Notification.class);
	}
	
	public void deleteNotification(String notificationId) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTH, apiKey);
		HttpEntity<String> request = new HttpEntity< >(notificationId, headers);
		restTemplate.exchange(getResource(notificationId), HttpMethod.DELETE, request, String.class);
	}
	
	public int getNumberNotifications(String userId) {
 		RestTemplate restTemplate = new RestTemplate();
 		HttpHeaders headers = new HttpHeaders();
 		headers.add(AUTH, apiKey);
 		HttpEntity<String> jwtEntity = new HttpEntity<>(headers);
 		ResponseEntity<Integer> userResponse = restTemplate.exchange(getResource(userId, "/number"), HttpMethod.GET, jwtEntity,
 				Integer.class);
 		return userResponse.getBody();
 	}
	
	public List<Notification> getUserNotifications(String userId) {
 		RestTemplate restTemplate = new RestTemplate();
 		HttpHeaders headers = new HttpHeaders();
 		headers.add(AUTH, apiKey);
 		HttpEntity<String> jwtEntity = new HttpEntity<>(headers);
 		ResponseEntity<List<Notification>> userResponse = restTemplate.exchange(getResource(userId, ""), HttpMethod.GET, jwtEntity,
 				 new ParameterizedTypeReference<List<Notification>>() {});
 		return userResponse.getBody();
 	}
	
	private String getResource(String userId, String path) {
 		if(userId.isEmpty()) {
 			userId = "";
 		}
 		else {
 			userId = "?userId=" + userId;
 		}
 		return (resource + path + userId);
 	}
	
	private String getResource (String notificationId){
		return resource + "/" + notificationId;
	}

}
