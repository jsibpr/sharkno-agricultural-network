package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;

public class LiteProfileMapper implements RowMapper<LiteProfile> {

	@Override
	public LiteProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			Float average = null;
			String averageString = rs.getString("averageReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			return new LiteProfile(rs.getString("pro_id"), rs.getString("name"), new AboutMe(rs.getString("abm_id"), rs.getString("shortDescription"), rs.getString("longDescription")), rs.getString("profilePicture")
				          , LiteProfile.Type.valueOf(rs.getString("type")), rs.getBigDecimal("salary"), rs.getString("email"), average, rs.getInt("likeQty"));
		} else {
			return null;
		}
		
	}

}