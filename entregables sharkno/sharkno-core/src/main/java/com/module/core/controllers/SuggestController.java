package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Address;
import com.module.core.models.Country;
import com.module.core.models.Entity;
import com.module.core.models.Session;
import com.module.core.services.SuggestService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.SUGGEST_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class SuggestController extends BaseController{

	@Autowired
	SuggestService suggestService;

	@GetMapping("/countries")
	@ApiOperation(value="Suggest Countries", authorizations = {@Authorization(value="basicAuth")})
	public List<Country> suggestCountries(@RequestParam String country, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session))  {
			return suggestService.suggestCountries(country);
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/citiesInCountry")
	@ApiOperation(value="Suggest Cities In Country", authorizations = {@Authorization(value="basicAuth")})
	public List<Address> suggestCitiesInCountry(@RequestParam String country, @RequestParam String city, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session))  {
			return suggestService.suggestCitiesInCountry(country, city);
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/cities")
	@ApiOperation(value="Suggest Cities", authorizations = {@Authorization(value="basicAuth")})
	public List<Address> suggestCities(@RequestParam String city, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session))  {
			return suggestService.suggestCities(city);
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/skills")
	@ApiOperation(value="Suggest Skills", authorizations = {@Authorization(value="basicAuth")})
	public List<Entity> suggestSkills(@RequestParam String fragment, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session))  {
			return suggestService.suggestEntities("SKILL", fragment);
		}
		return Collections.emptyList();
	}
	
	@GetMapping("/categories")
	@ApiOperation(value="Suggest Categories", authorizations = {@Authorization(value="basicAuth")})
	public List<Entity> suggestCategories(@RequestParam String fragment, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session))  {
			return suggestService.suggestEntities("CATEGORY", fragment);
		}
		return Collections.emptyList();
	}
	
}
