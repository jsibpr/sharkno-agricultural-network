package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Recommendation;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;

public class RecommendationMapper implements RowMapper<Recommendation> {

	@Override
	public Recommendation mapRow(ResultSet rs, int rowNum) throws SQLException{
		if (rs!= null) {
			Float average = null;
			String averageString = rs.getString("pro_averageReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			
			LiteProfile origin = new LiteProfile(rs.getString("pro_id"),rs.getString("pro_name"), new AboutMe(), rs.getString("pro_profPicture"), LiteProfile.Type.valueOf(rs.getString("pro_type")),
					 rs.getBigDecimal("pro_salary"), rs.getString("pro_email"), average, rs.getInt("likeQty"));
			return new Recommendation(rs.getString("rec_id"), origin, rs.getString("rec_destination"), rs.getString("rec_description"), rs.getDate("rec_creatDate"));
		} else {
			return null;
		}
	}
}