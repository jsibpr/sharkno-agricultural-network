package com.module.core.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.clients.NotificationClient;
import com.module.core.dao.repositories.RecommendationRepository;
import com.module.core.dao.repositories.ReviewRepository;
import com.module.core.models.Notification;
import com.module.core.models.Recommendation;
import com.module.core.models.Session;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.view.RecommendationForView;

@Service
@Transactional
public class RecommendationService {

	@Autowired
	RecommendationRepository recommendationRepository;

	@Autowired
	ReviewRepository reviewRepository;
	
	@Autowired
	NotificationClient notificationClient;
	
	@Autowired
	MailNotificationService mailNotificationService;

	public List<Recommendation> getRecommendations(String profileId) {
		return recommendationRepository.getRecommendations(profileId);
	}

	public void createRecommendation(RecommendationForView recommendationForView, String originId, Session session) {
		Recommendation recommendation = new Recommendation();
		recommendation.setId(UUID.randomUUID().toString().substring(0, 18));
		LiteProfile origin = new LiteProfile();
		origin.setId(originId);
		recommendation.setOrigin(origin);
		recommendation.setDestinationId(recommendationForView.getDestinationId());
		recommendation.setDescription(recommendationForView.getDescription());
		recommendation.setCreationDate(new Date());
		recommendationRepository.createRecommendation(recommendation);
		
		Notification notification = new Notification(UUID.randomUUID().toString().substring(0, 18),
				recommendation.getOrigin().getId(), "Nueva recommendación", new Date(),
				null, "RECOMMENDATION", recommendation.getId());
		notificationClient.createNotification(notification);
		
		mailNotificationService.notificationMail(notification);
		
	}

}