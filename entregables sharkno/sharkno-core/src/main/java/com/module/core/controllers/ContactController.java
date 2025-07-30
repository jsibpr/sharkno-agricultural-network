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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Contact;
import com.module.core.models.Session;
import com.module.core.services.ContactService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.CONTACT_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/contacts")
public class ContactController extends BaseController{
	
	@Autowired
	ContactService contactService;
	
	@GetMapping(path="/all")
	@ApiOperation(value="Get all logged User's contacts. Also check ProfileController/contacts, it includes more info about the user", authorizations = {@Authorization(value="basicAuth")})
	public List<Contact> getAll(@RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return contactService.getAll(idUser(session));
		}
		return Collections.emptyList();
	}

	@PutMapping(path="/add", consumes="application/json")
	@ApiOperation(value="Create contact request **FROM** the logged user (origin) **TO** the contactDestination (destination) user", authorizations = {@Authorization(value="basicAuth")})
	public void addContact(@RequestBody String contactDestination, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			contactService.addContact(session, contactDestination);
		}
	}
	
	@DeleteMapping(path="/{profileId}")
	@ApiOperation(value="Remove Contact. Not a SOFT DELETE \uD83D\uDC40", authorizations = {@Authorization(value="basicAuth")})
	public void removeContact(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			contactService.deleteContact(idUser(session), profileId);
		}	
	}
	
	@PostMapping(path="/{profileId}")
	@ApiOperation(value="Confirm Contact Request/Petition",
		notes = "Logged user will be the destination and the provided will be origin in the Contact relationship",
		authorizations = {@Authorization(value="basicAuth")})
	public void confirmContactPetition(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			contactService.confirmContact(idUser(session), profileId);
		}	
	}
	
	@PostMapping(path="reject/{profileId}")
	@ApiOperation(value="Reject Contact Request/Petition",
		notes = "Logged user will be the destination and the provided will be origin in the Contact relationship",
		authorizations = {@Authorization(value="basicAuth")})
	public void rejectContactPetition(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			contactService.rejectContact(idUser(session), profileId);
		}	
	}
	
	@GetMapping(path="/{profileId}")
	@ApiOperation(value="Check if users are contacts",
		notes="If the users are contacts a Contact.Status will be returned, otherwise null",
		authorizations = {@Authorization(value="basicAuth")})
	public Contact.Status areContacts(@PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return contactService.contactStatus(idUser(session), profileId);	
		}
		return null;
	}
	
}
