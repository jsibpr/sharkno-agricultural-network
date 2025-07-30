package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Conversation;
import com.module.core.models.Message;
import com.module.core.models.Session;
import com.module.core.models.view.MessageForView;
import com.module.core.services.MessageService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.MESSAGE_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/message")
public class MessageController extends BaseController {
	
	@Autowired
	MessageService messageService;
	
	@GetMapping
	@ApiOperation(value="Get logged user's Messages", authorizations = {@Authorization(value="basicAuth")})
	public List<Message> getMessages(@RequestParam String idProfile,@RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return messageService.getMessages(idUser(session), idProfile);
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/conversations")
	@ApiOperation(value="Get logged user's Conversations", authorizations = {@Authorization(value="basicAuth")})
	public List<Conversation> getConversations(@RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return messageService.getConversations(idUser(session));
		}
		return Collections.emptyList();
	}
	
	@PutMapping(path="/add", consumes="application/json")
	@ApiOperation(value="Create a Message FROM the logged user TO the destinationId user", authorizations = {@Authorization(value="basicAuth")})
	public void addContact(@RequestBody MessageForView messageForView, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			messageService.addMessage(messageForView, idUser(session));
		}
	}

}
