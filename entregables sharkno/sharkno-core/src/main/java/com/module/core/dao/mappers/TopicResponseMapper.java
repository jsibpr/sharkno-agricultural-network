package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.topic.Like;
import com.module.core.models.topic.TopicResponse;
import com.module.core.models.topic.Like.Status;

public class TopicResponseMapper implements RowMapper<TopicResponse>{

	@Override
	public TopicResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
		if(rs != null){
			AboutMe aboutMe = new AboutMe(rs.getString("abm_id"), rs.getString("shortDescription"), rs.getString("longDescription"));
			LiteProfile liteProfile = new LiteProfile(rs.getString("pro_id"), rs.getString("pro_name"), aboutMe, rs.getString("profilePicture"), LiteProfile.Type.valueOf(rs.getString("pro_type")), rs.getBigDecimal("salary"), rs.getString("email"), null, rs.getInt("pro_likeQty"));
			Like like = new Like(rs.getInt("likeQty"), rs.getString("status") != null ? Status.valueOf(rs.getString("status")) : null); 
					
			return new TopicResponse(rs.getString("id"), rs.getString("description"), liteProfile, rs.getDate("creationDate"), rs.getDate("lastUpdate"), rs.getString("topicId"), like);
		}else {
			return null;
		}
	}

}
