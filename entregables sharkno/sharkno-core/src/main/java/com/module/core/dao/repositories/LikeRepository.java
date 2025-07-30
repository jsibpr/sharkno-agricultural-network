package com.module.core.dao.repositories;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.BasicTopicMapper;
import com.module.core.dao.mappers.LiteProfileMapper;
import com.module.core.dao.mappers.ProfileMapper;
import com.module.core.dao.mappers.TopicResponseMapper;
import com.module.core.models.topic.TopicResponse;

@Repository
public class LikeRepository {

	@Autowired
	private JdbcTemplate template;
	
	private static final String LIKES_TABLE_INSERT = " likes ";
	private static final String LIKE_FIELDS = "originId, destination, type, creationDate";
	private static final String TOPICS_RESPONSES_TABLE = " topics_responses TOR ";
	private static final String PROFILES_TABLE = " profiles PRO ";
	
	private static final String WHERE = " WHERE ";
	private static final String COUNT = " COUNT";
	private static final String FROM = " FROM ";
	private static final String LIKES_TABLE = " likes LK";
	private static final String LEFT_JOIN = " LEFT JOIN ";
	
	public void createLike(String originId, String destination, String type, Date creationDate) {
		template.update("INSERT INTO " + LIKES_TABLE_INSERT + "(" + LIKE_FIELDS + ") VALUES (?, ?, ?, ?)",
				originId, destination, type, creationDate);
	}
	
	public void deleteLike(String origin, String destination) {
		template.update("DELETE FROM " + LIKES_TABLE_INSERT + WHERE + "originId = ? AND destination = ?", origin, destination);
	}
	
	public void updateCountLikeProfile(String profileId) {
		template.update("UPDATE " + PROFILES_TABLE + "SET PRO.likeQty = (SELECT " + COUNT + "(LK.destination) likeQty "
				+ FROM + TOPICS_RESPONSES_TABLE 
				+ LEFT_JOIN + LIKES_TABLE + " on TOR.id = LK.destination"
				+ " WHERE TOR.originId = ?)"
				+ " WHERE PRO.id = ?"
				,new Object[] {profileId, profileId});
	}
	
}
