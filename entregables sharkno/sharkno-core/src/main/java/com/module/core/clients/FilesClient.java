package com.module.core.clients;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.module.core.exceptions.ForbiddenException;
import com.module.core.exceptions.RepositoryException;
import com.module.core.exceptions.SharknoException;

@Service
public class FilesClient {
	@Value("${files.url}")
	private String resource;

	@Value("${files.apiKey}")
	private String apiKey;
	
	private static Logger LOG = LoggerFactory.getLogger(FilesClient.class);
	
	public String uploadFile(MultipartFile multipartFile) throws SharknoException{
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = getHeaders();
		File file = null;
		try {
			file = multipartToFile(multipartFile, multipartFile.getOriginalFilename());
		} catch (IOException e) {
			LOG.error("Error while converting MultipartFile into File");
			throw new RepositoryException(e);
		}
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(file));
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity
		 = new HttpEntity<>(body, headers);
		ResponseEntity<String> response;
		try {
		 response = restTemplate.postForEntity(resource, requestEntity, String.class);
		} catch (Exception e) {
			LOG.error("Error connecting to files module");
			throw new RepositoryException(e);
		}
		
		 if (response != null && response.getStatusCode()==HttpStatus.OK) {
			 LOG.info("Uploaded file " + file.getName() + " into repository");
			 return response.getBody();
		 } else {
			 if (response!=null) {
				 LOG.error("Files module responded with a status " + response.getStatusCodeValue() + " - " + response.getBody());
			 } else {
				 LOG.error("Null response from files module, check the server status");
			 }
			 throw new ForbiddenException();
		 }
		 
	}
	
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
		headers.set("Authorization", apiKey);
		return headers;
	}
	
	public static File multipartToFile(MultipartFile multipart, String fileName) throws IOException {
	    File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
	    multipart.transferTo(tempFile);
	    return tempFile;
	}

}
