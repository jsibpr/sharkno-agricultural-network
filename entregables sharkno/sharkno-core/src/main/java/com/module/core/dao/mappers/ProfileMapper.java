package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile.Type;
import com.module.core.models.profile.Profile;

public class ProfileMapper implements RowMapper<Profile> {

	@Override
	public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			Float average = null;
			String averageString = rs.getString("averageReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			return new Profile(rs.getString("pro_id"), rs.getString("name"), new AboutMe(rs.getString("abm_id"), rs.getString("shortDescription"), rs.getString("longDescription")), rs.getString("profilePicture")
				          , LocalDate.parse(rs.getString("birthDate"))
				          , rs.getString("email"), rs.getString("phone") 
				          , new Address(rs.getInt("address"), rs.getString("asciiname"), rs.getString("countryCode"))
				          , rs.getString("web"), null
				          , null, null, null, null, null
				          , Type.valueOf(rs.getString("type")), rs.getBigDecimal("salary"), average, rs.getInt("likeQty"));
		} else {
			return null;
		}
		
	}

}