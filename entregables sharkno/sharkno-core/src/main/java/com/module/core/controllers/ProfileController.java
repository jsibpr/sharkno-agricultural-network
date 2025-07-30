package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Segment;
import com.module.core.models.Session;
import com.module.core.models.profile.BasicProfile;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.profile.Profile;
import com.module.core.models.view.SegmentForView;
import com.module.core.services.EntityService;
import com.module.core.services.ProfileService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.PROFILE_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class ProfileController extends BaseController {

	@Autowired
	ProfileService profileService;
	
	@Autowired
	EntityService entityService;

	//**********BASIC PROFILE**********
	@GetMapping("/me")
	@ApiOperation(value="Get logged user profile", authorizations = {@Authorization(value="basicAuth")})
	public Profile getSelfProfile(@RequestHeader HttpHeaders headers) throws SharknoException { 
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getFullProfile(idUser(session));
		}
		return new Profile();
	}
	
	@GetMapping("/basicprofile")
	@ApiOperation(value="Basic Profile from logged user", authorizations = {@Authorization(value="basicAuth")})
	public BasicProfile getBasicProfile(@RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getBasicProfile(idUser(session));
		}
		return new BasicProfile();
	}
	
	@GetMapping("/liteprofile")
	@ApiOperation(value="Lite Profile from logged user", authorizations = {@Authorization(value="basicAuth")})
	public LiteProfile getLiteProfile(@RequestParam String id, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getLiteProfile(id);
		}
		return new LiteProfile();
	}
	
	@PostMapping(path="/basicprofile", consumes="application/json")
	@ApiOperation(value="Update Basic Profile from logged user", authorizations = {@Authorization(value="basicAuth")})
	public void updateBasicProfile(@RequestBody BasicProfile basicProfile, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.updateBasicProfile(basicProfile);
		}
	}
	
	@GetMapping("/contacts")
	@ApiOperation(value="Logged user contacts. This call includes profile info", authorizations = {@Authorization(value="basicAuth")})
	public List<LiteProfile> getContacts(@RequestParam String fragment, @RequestParam int limit, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getContacts(fragment,limit,idUser(session));
		}
		return Collections.emptyList();
	}
	
	//**********EXPERIENCE**********
	@GetMapping("/experience")
	@ApiOperation(value="Get Experience from given profile", authorizations = {@Authorization(value="basicAuth")})
	public List<Segment> getExperience(@RequestParam String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getSegment(profileId, Segment.Type.EXPERIENCE);
		}
		return Collections.emptyList();
	}
	
	@PostMapping(path="/experience", consumes="application/json")
	@ApiOperation(value="Update Experience", authorizations = {@Authorization(value="basicAuth")})
	public void updateExperience(@RequestBody Segment experience, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.updateSegment(experience, idUser(session));
		}
	}
	
	@PutMapping(path="/experience", consumes="application/json")
	@ApiOperation(value="Create Experience", authorizations = {@Authorization(value="basicAuth")})
	public void createExperience(@RequestBody SegmentForView experience, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.createSegment(experience, idUser(session), Segment.Type.EXPERIENCE);	
		}
	}
	
	@DeleteMapping(path="/experience")
	@ApiOperation(value="Delete Experience", authorizations = {@Authorization(value="basicAuth")})
	public void deleteExperience(@RequestParam String id, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.deleteSegment(id);
		}
	}
	
	//**********EDUCATION**********
	@GetMapping("/education")
	@ApiOperation(value="Get Education from given profile", authorizations = {@Authorization(value="basicAuth")})
	public List<Segment> getEducation(@RequestParam String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return profileService.getSegment(profileId, Segment.Type.EDUCATION);
		}
		return Collections.<Segment>emptyList();
	}
	
	@PostMapping(path="/education", consumes="application/json")
	@ApiOperation(value="Update Education", authorizations = {@Authorization(value="basicAuth")})
	public void updateEducation(@RequestBody Segment education, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.updateSegment(education, idUser(session));
		}
	}
	
	@PutMapping(path="/education", consumes="application/json")
	@ApiOperation(value="Create Education", authorizations = {@Authorization(value="basicAuth")})
	public void createEducation(@RequestBody SegmentForView education, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.createSegment(education, idUser(session), Segment.Type.EDUCATION);
		}
	}
	
	@DeleteMapping(path="/education")
	@ApiOperation(value="Delete Education", authorizations = {@Authorization(value="basicAuth")})
	public void deleteEducation(@RequestParam String id, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			profileService.deleteSegment(id);
		}
	}
	
	//**********SKILLS**********
	
	
	@PutMapping(path="/myskill", consumes="application/json")
	@ApiOperation(value="Add Skill", authorizations = {@Authorization(value="basicAuth")})
	public void addSkill(@RequestBody String skillId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.createEntityRelation(skillId, idUser(session), "SKILL");
		}
	}
	
	@DeleteMapping(path="/myskill")
	@ApiOperation(value="Delete Skill from Profile", authorizations = {@Authorization(value="basicAuth")})
	public void deleteMySkill(@RequestParam String skillId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.deleteEntityRelation(idUser(session), skillId);
		}
	}

}
