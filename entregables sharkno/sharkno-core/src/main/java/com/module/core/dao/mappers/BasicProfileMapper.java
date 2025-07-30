package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.BasicProfile;
import com.module.core.models.profile.LiteProfile.Type;

public class BasicProfileMapper implements RowMapper<BasicProfile> {

	@Override
	public BasicProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			Float average = null;
			String averageString = rs.getString("averageReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			return new BasicProfile(rs.getString("id"), rs.getString("name"), new AboutMe( rs.getString("ABM.id"),  rs.getString("ABM.shortDescription"),  rs.getString("ABM.longDescription"))
						 	   , rs.getString("profilePicture")
				               , rs.getString("birthDate") != null ? LocalDate.parse(rs.getString("birthDate")) : null
				               , rs.getString("email"), rs.getString("phone") 
				               , new Address(rs.getInt("address"), rs.getString("asciiname"), rs.getString("countryCode"))
				               , rs.getString("web"), null
				               , Type.valueOf(rs.getString("type")), rs.getBigDecimal("salary"), average, rs.getInt("likeQty"));
		} else {
			return null;
		}

	}

}