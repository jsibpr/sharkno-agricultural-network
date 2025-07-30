package com.module.core.controllers;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import com.module.core.SwaggerDocConfig;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.module.core.exceptions.SharknoException;
import com.module.core.models.Review;
import com.module.core.models.Session;
import com.module.core.models.view.ReviewForView;
import com.module.core.services.ReviewService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = {SwaggerDocConfig.REVIEW_CONTROLLER_TAG})
@CrossOrigin(origins = "*")
public class ReviewController extends BaseController{
	
	@Autowired
	ReviewService reviewService;
	
	@PutMapping(path="/review", consumes="application/json")
	@ApiOperation(value="Create Review. Can be called after service culmination", authorizations = {@Authorization(value="basicAuth")})
	public void createReview(@Valid @RequestBody ReviewForView review, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			reviewService.createReview(review, idUser(session));
		}
	}
	
	@GetMapping(path="review/{reviewType}/{profileId}")
	@ApiOperation(value="Get reviews of given type for selected user profile", authorizations = {@Authorization(value="basicAuth")})
	public List<Review> getReviews(@PathVariable String reviewType, @PathVariable String profileId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return reviewService.getReviews(reviewType, profileId);
		}
		return Collections.emptyList();
	}
	
	@GetMapping(path="review/{reviewType}/{profileId}/{serviceId}")
	@ApiOperation(value="Get reviews received for a specific service", authorizations = {@Authorization(value="basicAuth")})
	public List<Review> getReviewsByService(@PathVariable String reviewType, @PathVariable String profileId, @PathVariable String serviceId, @RequestHeader HttpHeaders headers) throws SharknoException {
		Session session = getSession(headers.getFirst(HttpHeaders.AUTHORIZATION));
		if (isAllow(session)) {
			return reviewService.getReviewsByService(reviewType, profileId, serviceId);
		}
		return Collections.emptyList();
	}

}