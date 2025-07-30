package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.ReviewMapper;
import com.module.core.models.Review;

@Repository
public class ReviewRepository {

	@Autowired
	JdbcTemplate template;

	private static final String PROFILES_TABLE = "profiles";
	private static final String SERVICES_TABLE = "services";
	private static final String REVIEWS_TABLE = "reviews";
	private static final String SERVICES_CANDIDATES_TABLE = "services_candidates";
	private static final String REVIEWS_FIELDS = "id, origin, destination, service, selfValuation, companyValuation, creationDate, skillValue,"
			+ " communicationValue, deadlineValue, availabilityValue, qualityValue, cooperationValue, type ";
	private static final String JOIN_FIELDS = "REV.id rev_id, REV.origin rev_origin, REV.destination rev_destination, REV.service rev_service,"
			+ " REV.selfValuation rev_selfValuation, REV.companyValuation rev_companyValuation, REV.creationDate rev_creatDate, REV.skillValue rev_skillValue,"
			+ " REV.communicationValue rev_commValue, REV.deadlineValue rev_deadlineValue, REV.availabilityValue rev_avaValue, REV.qualityValue rev_qualityValue,"
			+ " REV.cooperationValue rev_coopValue, REV.type rev_type, SER.title ser_title, PRO.name pro_name";
	private static final String AVERAGE_VALUES = "skillvalue + communicationValue + deadlineValue + availabilityValue + qualityValue + cooperationValue";
	private static final String UPDATE = "update ";

	public boolean createReview(Review review) {
		if(!hasReviewed(review.getOrigin().getId(), review.getService().getId(), review.getDestination())) {
			
		template.update(
				"INSERT INTO " + REVIEWS_TABLE + " (" + REVIEWS_FIELDS
						+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				review.getId(), review.getOrigin().getId(), review.getDestination(),
				review.getService().getId(), review.getSelfValuation(), review.getCompanyValuation(),
				review.getCreationDate(), review.getSkillValue(), review.getCommunicationValue(),
				review.getDeadlineValue(), review.getAvailabilityValue(), review.getQualityValue(),
				review.getCooperationValue(), review.getType().toString());
		return true;
		}else {
			return false;
		}
	}

	private boolean hasReviewed(String reviewOrigin, String serviceId, String reviewDestination) {
		return (template.queryForObject("SELECT count(origin) FROM " + REVIEWS_TABLE + " REV WHERE REV.origin = ? AND REV.service = ? AND REV.destination = ?",
				new Object[] { reviewOrigin, serviceId, reviewDestination }, Integer.class)) > 0;
	}

	public List<Review> getReviews(String reviewType, String profileId) {
		return template.query(
				"SELECT " + JOIN_FIELDS + " FROM " + REVIEWS_TABLE + " REV JOIN " + SERVICES_TABLE + " SER on REV.service = SER.id  JOIN " + PROFILES_TABLE +
				" PRO on REV.origin = PRO.id where REV.destination = ? AND REV.type = ? order by REV.creationDate desc",
				new Object[] { profileId, reviewType }, new ReviewMapper());
	}
	
	public List<Review> getReviewsByService(String reviewType, String profileId, String serviceId) {
		return template.query(
				"SELECT " + JOIN_FIELDS + " FROM " + REVIEWS_TABLE + " REV JOIN " + SERVICES_TABLE + " SER on REV.service = SER.id JOIN " 
		+ PROFILES_TABLE + " PRO on REV.origin = PRO.id where REV.destination = ? AND REV.service = ? AND REV.type = ?",
				new Object[] { profileId, serviceId, reviewType }, new ReviewMapper());
	}

	public void updateAverageReview(String destinationId, String serviceId) {
		if(isCandidate(destinationId)) {
			Float average = template.queryForObject("SELECT  (select AVG((" + AVERAGE_VALUES + ")/6) " + "as averageReview FROM "
				+ REVIEWS_TABLE + " REV WHERE R.destination = REV.destination) as averageReview FROM " + REVIEWS_TABLE
				+ " R WHERE R.destination = ? LIMIT ?", new Object[] { destinationId, 1 }, Float.class);
		
		template.update(UPDATE + PROFILES_TABLE + " SET averageReview = ? WHERE id = ?", average, destinationId);
		template.update(UPDATE + SERVICES_CANDIDATES_TABLE + " SET status = ? WHERE profileId = ? AND status = ? AND serviceId = ?", "EVALUATED", destinationId, "ACCEPTED", serviceId);
		
		}else {
			updateAverageServiceReview(serviceId);
			Float businessReviewsAverage = template.queryForObject("SELECT AVG(averageServiceReview) as businessReviewsAverage FROM " 
		+ SERVICES_TABLE + " WHERE origin = ?", new Object[] { destinationId}, Float.class);
			
			template.update(UPDATE + PROFILES_TABLE + " SET averageReview = ? WHERE id = ?", businessReviewsAverage, destinationId);
		}
	}

	private boolean isCandidate(String destinationId) {
		return (template.queryForObject("SELECT count(profileId) FROM " + SERVICES_CANDIDATES_TABLE +
				" SERCAN WHERE SERCAN.profileId = ? AND SERCAN.status = ?", new Object[] { destinationId, "ACCEPTED" }, Integer.class)) > 0;
	}

	public void updateAverageServiceReview(String serviceId) {
		Float serviceAverage = template.queryForObject("SELECT  (select AVG((" + AVERAGE_VALUES + ")/6) " + "as averageServiceReview FROM "
				+ REVIEWS_TABLE + " REV WHERE R.service = REV.service) as averageServiceReview FROM " + REVIEWS_TABLE
				+ " R WHERE R.service = ? AND R.type = ? LIMIT ?;", new Object[] { serviceId, "EMPLOYEE", 1 },Float.class);
			
		template.update(UPDATE + SERVICES_TABLE + " SET averageServiceReview = ? WHERE id = ?", serviceAverage, serviceId);
	}
	
}