package com.module.core.controllers;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Session;
import com.module.core.services.EntityService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.ENTITY_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class EntityController extends BaseController{

	@Autowired
	EntityService entityService;
	
	@PutMapping(path="/skills", consumes="application/json")
	@ApiOperation(value="Create Skill", authorizations = {@Authorization(value="basicAuth")})
	public String createSkill(@RequestBody String skillName, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return entityService.createEntity(skillName, "SKILL");
		}
		return null;
	}
	
	
	@PutMapping(path="/categories", consumes="application/json")
	@ApiOperation(value="Create Category", authorizations = {@Authorization(value="basicAuth")})
	public void createCategory(@RequestBody String name, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.createEntity(name, "CATEGORY");
		}
	}

}
