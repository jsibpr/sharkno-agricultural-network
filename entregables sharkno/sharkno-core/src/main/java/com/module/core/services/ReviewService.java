package com.module.core.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.ReviewRepository;
import com.module.core.exceptions.ForbiddenException;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.Review;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;
import com.module.core.models.view.ReviewForView;

@Service
@Transactional
public class ReviewService {

	@Autowired
	ReviewRepository reviewRepository;

	public List<Review> getReviews(String reviewType, String profileId) {
		return reviewRepository.getReviews(reviewType, profileId);
	}
	
	public List<Review> getReviewsByService(String reviewType, String profileId, String serviceId) {
		return reviewRepository.getReviewsByService(reviewType, profileId, serviceId);
	}

	/**
	 * @param reviewForView
	 * @param originId
	 * @throws SharknoException 
	 */
	public void createReview(ReviewForView reviewForView, String originId) throws SharknoException {
		Review review = new Review();
		review.setId(UUID.randomUUID().toString().substring(0, 18));
		LiteProfile origin = new LiteProfile();
		origin.setId(originId);
		review.setOrigin(origin);
		review.setDestination(reviewForView.getDestinationId());
		BasicService service = new BasicService();
		service.setId(reviewForView.getServiceId());
		review.setService(service);
		review.setSelfValuation(reviewForView.getSelfValuation());
		review.setCompanyValuation(reviewForView.getCompanyValuation());
		review.setCreationDate(new Date());
		review.setSkillValue(reviewForView.getSkillValue());
		review.setCommunicationValue(reviewForView.getCommunicationValue());
		review.setDeadlineValue(reviewForView.getDeadlineValue());
		review.setAvailabilityValue(reviewForView.getAvailabilityValue());
		review.setQualityValue(reviewForView.getQualityValue());
		review.setCooperationValue(reviewForView.getCooperationValue());
		review.setType(reviewForView.getType());

		if(reviewRepository.createReview(review)) {
			reviewRepository.updateAverageReview(reviewForView.getDestinationId(), reviewForView.getServiceId());			
		}else {
			throw new ForbiddenException();
		}
	}

}