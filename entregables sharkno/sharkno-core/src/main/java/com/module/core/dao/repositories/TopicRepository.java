package com.module.core.dao.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.BasicTopicMapper;
import com.module.core.dao.mappers.TopicResponseMapper;
import com.module.core.models.topic.BasicTopic;
import com.module.core.models.topic.Topic;
import com.module.core.models.topic.TopicResponse;
import com.module.core.models.view.TopicSearchParams;

@Repository
public class TopicRepository {

	@Autowired
	private JdbcTemplate template;
	
	//**********Topic**********
	private static final String TOPICS_TABLE = " topics TPI ";
	private static final String TOPICS_TABLE_INSERT = " topics ";
	private static final String TOPIC_FIELDS = "id, title, description, categoryId, originId, creationDate, lastUpdate";
	private static final String TOPIC_UPDATE_FIELDS = "title = ?, description = ?, categoryId = ?, lastUpdate = ?";
	private static final String TOPICS_RESPONSES_TABLE = " topics_responses TOR ";
	private static final String TOPICS_RESPONSES_TABLE_INSERT = " topics_responses ";
	private static final String TOPIC_RESPONSE_FIELDS = "id, description, originId, creationDate, lastUpdate, topicId";
	private static final String TOPIC_RESPONSE_UPDATE_FIELDS = "description = ?, lastUpdate = ?";
	private static final String ENTITIES_RELATIONS_TABLE = " entities_relations ENTREL ";
	private static final String LIKES_TABLE = "likes LK ";
	
	//*****Topic Response******
	private static final String TOPIC_RESPONSE_TABLE = " topics_responses TOR ";
	
	//*********Entity**********
	private static final String ENTITY_TABLE = " entities EN ";
	
	//*********Profile*********
	private static final String PROFILES_TABLE = " profiles PRO ";
	private static final String ABOUT_ME_TABLE = " aboutme ABM ";

	private static final String WHERE_ID = " WHERE id = ?";
	private static final String FROM = " FROM ";
	private static final String LEFT_JOIN = " LEFT JOIN ";
	private static final String COUNT = " COUNT";
	private static final String GROUP_BY = " GROUP BY ";
	private static final String ORDER_BY = " ORDER BY ";
	private static final String CASE = " CASE ";
	
	public List<Topic> getAllBasicTopic() {
		return template.query("SELECT TPI.*, EN.id en_id, EN.name, PRO.id pro_id, PRO.name pro_name, PRO.profilePicture, PRO.salary, PRO.email, PRO.likeQty pro_likeQty,"
				+ "ABM.id abm_id, ABM.shortDescription, ABM.longDescription,"
				+ COUNT + "(TOR.id) responsesQty "
				+ FROM + TOPICS_TABLE 
				+ LEFT_JOIN + ENTITY_TABLE + "ON TPI.categoryId = EN.id"
				+ LEFT_JOIN + PROFILES_TABLE + "ON TPI.originId = PRO.id"
				+ LEFT_JOIN + ABOUT_ME_TABLE + "ON PRO.id = ABM.profileId"
				+ LEFT_JOIN + TOPIC_RESPONSE_TABLE + "on TPI.id = TOR.topicId"
				+ GROUP_BY + "TPI.id"
				, new BasicTopicMapper());
	}
	
	public BasicTopic getBasicTopic(String id) {
		try {
			return template.queryForObject("SELECT TPI.*, EN.id en_id, EN.name, PRO.id pro_id, PRO.name pro_name, PRO.profilePicture, PRO.salary, PRO.email, PRO.likeQty pro_likeQty,"
					+ "ABM.id abm_id, ABM.shortDescription, ABM.longDescription,"
					+ COUNT + "(TOR.id) responsesQty "
					+ FROM + TOPICS_TABLE 
					+ LEFT_JOIN + ENTITY_TABLE + "ON TPI.categoryId = EN.id"
					+ LEFT_JOIN + PROFILES_TABLE + "ON TPI.originId = PRO.id"
					+ LEFT_JOIN + ABOUT_ME_TABLE + "ON PRO.id = ABM.profileId"
					+ LEFT_JOIN + TOPIC_RESPONSE_TABLE + "on TPI.id = TOR.topicId"
					+ " WHERE TPI.id = ?"
					+ GROUP_BY + "TPI.id"
					,new Object[] {id}, new BasicTopicMapper());
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public void createBasicTopic(BasicTopic basictopic) {
		template.update(
				"INSERT INTO" + TOPICS_TABLE_INSERT + "(" + TOPIC_FIELDS 
						+ ") VALUES (?, ?, ?, ?, ?, ?, ?)", 
						basictopic.getId(), basictopic.getTitle(), basictopic.getDescription(),
						basictopic.getCategory().getId(), basictopic.getOrigin().getId(),
						basictopic.getCreationDate(), basictopic.getLastUpdate());
	}
	
	public void updateBasicTopic(BasicTopic basictopic) {
		template.update("UPDATE " + TOPICS_TABLE_INSERT + " set " + TOPIC_UPDATE_FIELDS + WHERE_ID, basictopic.getTitle(),
				basictopic.getDescription(), basictopic.getCategory().getId(), basictopic.getLastUpdate(), basictopic.getId());
	}
	
	public void deleteBasicTopic(String id) {
		template.update("delete " + TOPICS_TABLE_INSERT + WHERE_ID, id);
	}
	
	public List<Topic> searchTopics(TopicSearchParams params){
		String likeParameter = params.getTitle() != null ? "%" + params.getTitle() + "%" : null;
		String statement = "SELECT TPI.*, EN.id en_id, EN.name, PRO.id pro_id, PRO.name pro_name, PRO.profilePicture, PRO.salary, PRO.email, PRO.likeQty pro_likeQty,"
				+ "ABM.id abm_id, ABM.shortDescription, ABM.longDescription,"
				.concat(COUNT + "(TOR.id) responsesQty ")
				.concat(FROM + TOPICS_TABLE
				.concat(LEFT_JOIN + ENTITY_TABLE + "ON TPI.categoryId = EN.id")
				.concat(LEFT_JOIN + PROFILES_TABLE + "ON TPI.originId = PRO.id")
				.concat(LEFT_JOIN + ABOUT_ME_TABLE + "ON PRO.id = ABM.profileId")
				.concat(LEFT_JOIN + TOPIC_RESPONSE_TABLE + "on TPI.id = TOR.topicId")
				.concat(" WHERE 1 = 1 ")
				.concat(params.getTitle() != null ? " AND TPI.title LIKE ?" : "")
				.concat(params.getOriginId() != null ? " AND TPI.originId LIKE ?" : "")
				.concat(params.getCategoryId() != null ? " AND TPI.categoryId LIKE ?" : "")
				.concat(params.getSkillId() != null ? " AND TPI.id in (SELECT id"+ FROM + ENTITIES_RELATIONS_TABLE  + "WHERE entityId = ? AND entityUse = 'SKILL')" : "")
				.concat(GROUP_BY + " TPI.id "))
				.concat(ORDER_BY + " creationDate DESC");
		List<Object> parameterList = new ArrayList<>();
		addToListIfNotNull(parameterList, likeParameter);
		addToListIfNotNull(parameterList, params.getOriginId());
		addToListIfNotNull(parameterList, params.getCategoryId());
		addToListIfNotNull(parameterList, params.getSkillId());
		return template.query(statement, parameterList.toArray(), new BasicTopicMapper());
	}
	
	public TopicResponse getTopicResponse(String id) {
		try {
			// NULL AS status is temporally, is for time limit.
			return template.queryForObject("select TOR.*, PRO.id pro_id, PRO.name pro_name, PRO.profilePicture, PRO.type pro_type, PRO.salary, PRO.email, PRO.likeQty pro_likeQty,"
					+ "ABM.id abm_id, ABM.shortDescription, ABM.longDescription, NULL AS status,"
					+ COUNT + "(LK.destination) likeQty "
					+ FROM + TOPICS_RESPONSES_TABLE
					+ LEFT_JOIN + PROFILES_TABLE + "on TOR.originId = PRO.id"
					+ LEFT_JOIN + ABOUT_ME_TABLE + "ON PRO.id = ABM.profileId"
					+ LEFT_JOIN + LIKES_TABLE + "ON TOR.id = LK.destination"
					+ " where TOR.id = ?"
					+ GROUP_BY + "TOR.id"
					,new Object[] {id}, new TopicResponseMapper());
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<TopicResponse> getResponseFromTopic(String id, String userId) {
		return template.query("SELECT TOR.*, PRO.id pro_id, PRO.name pro_name, PRO.profilePicture, PRO.type pro_type, PRO.salary, PRO.email, PRO.likeQty pro_likeQty,"
				+ "ABM.id abm_id, ABM.shortDescription, ABM.longDescription,"
				+ COUNT + "(LK.destination) likeQty, "
				+ CASE + " WHEN LK.destination = TOR.id AND LK.originId = ? THEN \"LIKED\" else \"NOT_LIKED\" END AS status"
				+ FROM + TOPICS_RESPONSES_TABLE 
				+ LEFT_JOIN + PROFILES_TABLE + "ON TOR.originId = PRO.id" 
				+ LEFT_JOIN + ABOUT_ME_TABLE + "ON PRO.id = ABM.profileId"
				+ LEFT_JOIN + LIKES_TABLE + "ON TOR.id = LK.destination"
				+ " where TOR.topicId = ?"
				+ GROUP_BY + "TOR.id"
				+ ORDER_BY + "likeQty DESC"
				,new Object[] {userId, id}, new TopicResponseMapper());
	}

	public void createTopicResponse(TopicResponse topicresponse) {
		template.update("INSERT INTO " + TOPICS_RESPONSES_TABLE_INSERT + "(" + TOPIC_RESPONSE_FIELDS + ") values (?, ?, ?, ?, ?, ?)", topicresponse.getId(),
				topicresponse.getDescription(), topicresponse.getOrigin().getId(), topicresponse.getCreationDate(), 
				topicresponse.getLastUpdate(), topicresponse.getTopidId());
	}
	
	public void updateTopicResponse(TopicResponse topicresponse) {
		template.update("UPDATE " + TOPICS_RESPONSES_TABLE_INSERT + " set " + TOPIC_RESPONSE_UPDATE_FIELDS + WHERE_ID, topicresponse.getDescription(), 
				topicresponse.getLastUpdate(), topicresponse.getId());
	}
	
	public void deleteTopicResponse(String id) {
		template.update("DELETE " + TOPICS_RESPONSES_TABLE_INSERT + WHERE_ID, id);
	}
	
	private void addToListIfNotNull(List<Object> list, Object object) {
		if (object != null) {
			list.add(object);
		}
	}
}
