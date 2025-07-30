package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Session;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.profile.PublicProfile;
import com.module.core.models.view.ProfileSearchParams;
import com.module.core.services.ProfileService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.PROFILE_SEARCH_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class ProfileSearchController extends BaseController{

	@Autowired
	ProfileService profileService;
	
	@GetMapping("/profile/{id}")
	@ApiOperation(value="Get Profile with given id", authorizations = {@Authorization(value="basicAuth")})
	public PublicProfile getProfile(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getPublicProfile(id, idUser(session));
		}
		return new PublicProfile();
	}
	
	@PostMapping(path="/search", consumes="application/json")
	@ApiOperation(value="Search Profiles", authorizations = {@Authorization(value="basicAuth")})
	public List<LiteProfile> searchProfiles(@RequestBody ProfileSearchParams params, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.searchProfiles(params, idUser(session));
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/suggestProfiles/{serviceId}")
	@ApiOperation(value="Profiles sorted by 'best match' for the current taking in count skills, address, etc", authorizations = {@Authorization(value="basicAuth")})
	public List<LiteProfile> suggestProfiles(@PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.suggestProfilesForService(serviceId);
		}
		return Collections.emptyList();
	}

}
