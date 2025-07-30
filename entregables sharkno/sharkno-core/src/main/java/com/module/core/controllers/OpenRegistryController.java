package com.module.core.controllers;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.User;
import com.module.core.services.OpenRegistryService;

import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = {SwaggerDocConfig.OPEN_REGISTRY_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/openRegistry")
public class OpenRegistryController extends BaseController {
	
	@Autowired
	OpenRegistryService openRegistrationService;
	
	@PutMapping(consumes="application/json")
	@ApiOperation(value="Create User using open registry")
	public void createUser(@RequestBody User user, @RequestHeader HttpHeaders headers) throws SharknoException {
			openRegistrationService.openRegistry(user);
	}
	
	//Post activate
	@PostMapping(path="/activateUser", consumes="application/json")
	@ApiOperation(value="Activate User")
	public void activateUser(@RequestBody String activateToken) throws SharknoException {
		openRegistrationService.activateUser(activateToken);
	}

}
