package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.RecommendationMapper;
import com.module.core.models.Recommendation;

@Repository
public class RecommendationRepository {

	@Autowired
	JdbcTemplate template;
	
	private static final String RECOMMENDATIONS_TABLE = " recommendations ";
	private static final String RECOMMENDATIONS_FIELDS = "id, origin, destination, description, creationDate";
	private static final String JOIN_FIELDS = "REC.id rec_id, REC.origin rec_origin, REC.destination rec_destination, REC.description rec_description, REC.creationDate rec_creatDate," + 
					 		" PRO.id pro_id, PRO.name pro_name, PRO.birthDate pro_bday, PRO.email pro_email, PRO.phone pro_phone, PRO.address pro_address, PRO.profilePicture pro_profPicture,"
					 		+ " PRO.type pro_type, PRO.creationDate pro_creatDate, PRO.lastUpdate pro_lastUpdate, PRO.salary pro_salary, PRO.averageReview pro_averageReview, PRO.likeQty";
	
	public void createRecommendation (Recommendation recommendation) {
		template.update("INSERT INTO " + RECOMMENDATIONS_TABLE + " (" + RECOMMENDATIONS_FIELDS + ") VALUES (?, ?, ?, ?, ?)",
				recommendation.getId(), recommendation.getOrigin().getId(), recommendation.getDestinationId(), recommendation.getDescription(), recommendation.getCreationDate());
	}

	public List<Recommendation> getRecommendations(String profileId) {
		return template.query("SELECT "+ JOIN_FIELDS + " FROM" + RECOMMENDATIONS_TABLE + " REC JOIN profiles PRO on REC.origin = PRO.ID where REC.destination = ? order by REC.creationDate desc",
				new Object[] {profileId}, new RecommendationMapper());
	}

}



