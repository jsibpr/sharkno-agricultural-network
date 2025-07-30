package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Entity;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.topic.Topic;

public class BasicTopicMapper implements RowMapper<Topic>{

	@Override
	public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
		if(rs != null) {
			Entity entity = new Entity(rs.getString("en_id"), rs.getString("name"));
			AboutMe aboutMe = new AboutMe(rs.getString("abm_id"), rs.getString("shortDescription"), rs.getString("longDescription"));
			LiteProfile liteProfile = new LiteProfile(rs.getString("pro_id"), rs.getString("pro_name"), aboutMe, rs.getString("profilePicture"), null, rs.getBigDecimal("salary"), rs.getString("email"), null, rs.getInt("pro_likeQty"));
			
			return new Topic(rs.getString("id"), rs.getString("title"), rs.getString("description"), entity, null, liteProfile, rs.getDate("creationDate"), rs.getDate("lastUpdate"), rs.getInt("responsesQty"), null);
		}
		else {
			return null;
		}
	}

}
