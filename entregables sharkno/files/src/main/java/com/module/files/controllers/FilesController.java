package com.module.files.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.module.files.SwaggerDocConfig;
import com.module.files.exceptions.AuthenticationException;
import com.module.files.exceptions.FilesException;
import com.module.files.services.FilesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.FILES_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/files")
public class FilesController {

	@Autowired
	FilesService filesService;
	
	@Value("${api.key}")
	private String apiKey;
	
	@PostMapping(consumes="multipart/form-data")
	@ApiOperation(value="Upload multipart file into a repository and return the public URL to access the file", authorizations = {@Authorization(value="basicAuth")})
	public String uploadFile(@RequestBody MultipartFile file, @RequestHeader HttpHeaders headers) throws FilesException {
		if (isAllow(headers.getFirst(HttpHeaders.AUTHORIZATION))) {
			return filesService.uploadFile(file);
		}
			return null;
	}
	
	private boolean isAllow (String key) throws FilesException{
		if (apiKey.equals(key)) {
			return true;
		}
		else {
			throw new AuthenticationException();
		}
	}
	

}