package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.Candidate;

public class CandidateMapper implements RowMapper<Candidate> {

	@Override
	public Candidate mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			Float average = null;
			String averageString = rs.getString("PRO.averageReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			return new Candidate(rs.getString("SERCAN.id"), new LiteProfile(rs.getString("profileId"), rs.getString("name")
					            , new AboutMe(rs.getString("id"),rs.getString("shortDescription"), rs.getString("longDescription"))
					            , rs.getString("profilePicture"), LiteProfile.Type.valueOf(rs.getString("type")), rs.getBigDecimal("salary"), rs.getString("email"), average, rs.getInt("likeQty"))
					            , Candidate.Status.valueOf(rs.getString("status")), rs.getString("SERCAN.serviceId"));

		} else {
			return null;
		}			
	}
}
