package com.module.core.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.SwaggerDocConfig;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.Session;
import com.module.core.models.topic.BasicTopic;
import com.module.core.models.topic.Topic;
import com.module.core.models.topic.TopicResponse;
import com.module.core.models.view.TopicForView;
import com.module.core.models.view.TopicSearchParams;
import com.module.core.models.view.TopicResponseForView;
import com.module.core.services.EntityService;
import com.module.core.services.LikeService;
import com.module.core.services.TopicService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.TOPIC_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/topics")
public class TopicController extends BaseController{
	
	@Autowired
	TopicService topicService;
	
	@Autowired
	EntityService entityService;

	@Autowired
	LikeService likeService;
	
	//********* BASIC TOPIC ************
	@PutMapping(consumes="application/json")
	@ApiOperation(value="Create Topic", authorizations = {@Authorization(value="basicAuth")})
	public void createBasicTopic(@RequestBody TopicForView topicForView, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.createBasicTopic(topicForView, session.getUser().getId());
		}
	}
	
	@PostMapping(consumes="application/json")
	@ApiOperation(value="Update Topic", authorizations = {@Authorization(value="basicAuth")})
	public void updateBasicTopic(@RequestBody TopicForView topicForView, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.updateBasicTopic(topicForView);
		}
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value="Get Basic Topic from id", authorizations = {@Authorization(value="basicAuth")})
	public BasicTopic getBasicTopic(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return topicService.getBasicTopic(id);
		}
		
		return new BasicTopic();
	}

	@GetMapping()
	@ApiOperation(value="Get all Basic Topic", authorizations = {@Authorization(value="basicAuth")})
	public List<Topic> getBasicTopics(@RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return topicService.getBasicTopics();
		}
		
		return new ArrayList<Topic>();
	}
	
	@DeleteMapping("/{id}")
	@ApiOperation(value="Delete Basic Topic for id", authorizations = {@Authorization(value="basicAuth")})
	public void deleteBasicTopic(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.deleteBasicTopic(id);
		}
	}

	@PutMapping(path="/skill", consumes="application/json")
	@ApiOperation(value="Add Skill to Topic", authorizations = {@Authorization(value="basicAuth")})
	public void addSkill(@RequestBody String skillId,  @RequestParam String topicId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.createEntityRelation(topicId, skillId, "SKILL");
		}
	}

	@DeleteMapping(path="/skill")
	@ApiOperation(value="Delete Skill from Topic. Not a SOFT DELETE \uD83D\uDC40",
		notes ="Use with CAUTION \uD83D\uDC40",
		authorizations = {@Authorization(value="basicAuth")})
	public void deleteMySkill(@RequestParam String skillId, @RequestParam String topicId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			entityService.deleteEntityRelation(topicId, skillId);
		}
	}

	@PutMapping(path="/like/{topicResponseId}")
	@ApiOperation(value="Add like to TopicResponse", authorizations = {@Authorization(value="basicAuth")})
	public void addLike(@PathVariable String topicResponseId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			likeService.createLike(session.getUser().getId(), topicResponseId, "TOPIC_RESPONSE");
		}
	}

	@DeleteMapping(path="/like/{topicResponseId}")
	@ApiOperation(value="Delete Like from Response", authorizations = {@Authorization(value="basicAuth")})
	public void deleteLike(@PathVariable String topicResponseId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			likeService.deleteLike(session.getUser().getId(), topicResponseId);
		}
	}

	@PostMapping(path="/search", consumes="application/json")
	@ApiOperation(value="Search Topics", authorizations = {@Authorization(value="basicAuth")})
	public List<Topic> searchTopics(@RequestBody TopicSearchParams params, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return topicService.searchTopics(params);
		}
		return Collections.emptyList();
	}
	
	//******** TOPIC RESPONSE **********
	@PutMapping(path="/response", consumes="application/json")
	@ApiOperation(value="Create Topic Response", authorizations = {@Authorization(value="basicAuth")})
	public void createTopicResponse(@RequestBody TopicResponseForView topicResponseForView, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.createTopicResponse(topicResponseForView, session.getUser().getId());
		}
	}
	
	@PostMapping(path="/response", consumes="application/json")
	@ApiOperation(value="Update Topic Response", authorizations = {@Authorization(value="basicAuth")})
	public void updateTopicResponse(@RequestBody TopicResponseForView topicResponseForView, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.updateTopicResponse(topicResponseForView);
		}
	}
	
	@GetMapping("/response/{id}")
	@ApiOperation(value="Get Topic Response from id", authorizations = {@Authorization(value="basicAuth")})
	public TopicResponse getTopicResponse(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return topicService.getTopicResponse(id);
		}
		
		return new TopicResponse();
	}

	@GetMapping("/responseFromTopic/{id}")
	@ApiOperation(value="Get all responses from topic", authorizations = {@Authorization(value="basicAuth")})
	public List<TopicResponse> getResponseFromTopic(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return topicService.getResponseFromTopic(id, session.getUser().getId());
		}
		
		return new ArrayList<TopicResponse>();
	}

	@DeleteMapping("/response/{id}")
	@ApiOperation(value="Delete Topic Response for id", authorizations = {@Authorization(value="basicAuth")})
	public void deleteTopicResponse(@PathVariable String id, @RequestHeader HttpHeaders headers) throws SharknoException{
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			topicService.deleteTopicResponse(id);
		}
	}
}
