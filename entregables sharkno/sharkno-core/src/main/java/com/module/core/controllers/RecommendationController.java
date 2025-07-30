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
import com.module.core.models.view.RecommendationForView;
import com.module.core.services.RecommendationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.RECOMMENDATION_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class RecommendationController extends BaseController{
	
	@Autowired
	RecommendationService recommendationService;
	
	@PutMapping(path="/recommendation", consumes="application/json")
	@ApiOperation(value="Create Recommendation", authorizations = {@Authorization(value="basicAuth")})
	public void createRecommendation(@RequestBody RecommendationForView recommendation, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			recommendationService.createRecommendation(recommendation, idUser(session), session);
		}
	}
}