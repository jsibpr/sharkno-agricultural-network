package com.module.mail.controllers;

import com.module.mail.SwaggerDocConfig;
import com.module.mail.exceptions.AuthenticationException;
import com.module.mail.exceptions.MailException;
import com.module.mail.models.Mail;
import com.module.mail.services.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@Api(tags = {SwaggerDocConfig.MAIL_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
@RequestMapping("/mail")
public class MailController {

	@Autowired
	MailService mailService;
	
	@Value("${api.key}")
	private String apiKey;
	
	@PutMapping(consumes="application/json")
	@ApiOperation(value="Tries to send Mail",
		notes="Upon failed email dispatch it wil be marked to be sent again using a scheduler.\n" +
			"The number of attempts is configurable via Spring properties.",
		authorizations = {@Authorization(value="basicAuth")})
	public void sendMail(@RequestBody Mail mail, @RequestHeader HttpHeaders headers) throws MailException {
		if (isAllow(headers.getFirst(HttpHeaders.AUTHORIZATION))) {
			mailService.sendMail(mail);	
		}
	}
	
	@DeleteMapping
	@ApiOperation(value="Deletes Mail from Database. This method **DELETES** the Mail from DB, not a soft delete \uD83D\uDC40",
		notes="Use with **caution**, since is there is no way to restore the object \uD83D\uDC40",
		authorizations = {@Authorization(value="basicAuth")})
	public void deleteMail(@RequestParam String mailId, @RequestHeader HttpHeaders headers) throws MailException {
		if (isAllow(headers.getFirst(HttpHeaders.AUTHORIZATION))) {
			mailService.deleteMail(mailId);
		}
	}
	
	@PostMapping
	@ApiOperation(value="Updates Mail in database", authorizations = {@Authorization(value="basicAuth")})
	public void updateMail(@RequestBody Mail mail, @RequestHeader HttpHeaders headers) throws MailException {
		if (isAllow(headers.getFirst(HttpHeaders.AUTHORIZATION))) {
			mailService.updateMail(mail);
		}
	}
	
	@GetMapping
	@ApiOperation(value="Retrieves all Mails", authorizations = {@Authorization(value="basicAuth")})
	public List<Mail> getMails(@RequestHeader HttpHeaders headers) throws MailException {
		if (isAllow(headers.getFirst(HttpHeaders.AUTHORIZATION))) {
			return mailService.getMails();
		}
		return Collections.emptyList();
	}
	
	
	private boolean isAllow (String key) throws MailException{
		if (apiKey.equals(key)) {
			return true;
		}
		else {
			throw new AuthenticationException();
		}
	}
	

}