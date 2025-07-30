package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;

public class BasicServiceMapper implements RowMapper<BasicService> {

	@Override
	public BasicService mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			Float serviceAverage = null;
			String serviceAVGString = rs.getString("averageServiceReview");
			
			if(serviceAVGString != null) {
				serviceAverage = Float.parseFloat(serviceAVGString);
			}
			
			Address address = new Address(rs.getInt("address"), rs.getString("asciiname"), rs.getString("countryCode")); 

			return new BasicService(rs.getString("id"), rs.getString("title"), rs.getString("description"), rs.getDate("creationDate"), rs.getDate("lastUpdate"),
					address, BasicService.Status.valueOf(rs.getString("status")), LiteProfile.Type.valueOf(rs.getString("candidateType")), serviceAverage);
		} else {
			return null;
		}
	}
}
