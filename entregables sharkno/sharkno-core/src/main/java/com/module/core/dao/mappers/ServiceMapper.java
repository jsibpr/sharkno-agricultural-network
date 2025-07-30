package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.profile.AboutMe;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService.Status;
import com.module.core.models.service.Payment;
import com.module.core.models.service.Service;

public class ServiceMapper implements RowMapper<Service> {
	@Override
	public Service mapRow(ResultSet rs, int rowNum) throws SQLException {

		if (rs != null) {
			Float average = null;
			Float serviceAverage = null;
			String averageString = rs.getString("averageReview");
			String serviceAVGString = rs.getString("averageServiceReview");
			if(averageString != null) {
				average = Float.parseFloat(averageString);
			}
			
			if(serviceAVGString != null) {
				serviceAverage = Float.parseFloat(serviceAVGString);
			}
			
			
			Address address = new Address(rs.getInt("address"), rs.getString("asciiname"), rs.getString("countryCode")); 
			Entity category = new Entity(rs.getString("category"),rs.getString("categoryName"));
			Payment payment = new Payment();
			if (rs.getString("paymentType") != null) { //External services have no payment
				payment = new Payment(Payment.Type.valueOf(rs.getString("paymentType")), rs.getBigDecimal("minAmount"), rs.getBigDecimal("maxAmount"), rs.getString("currency"));
			}
			AboutMe aboutMe = new AboutMe(rs.getString("aboutMeid"), rs.getString("shortDescription"), rs.getString("longDescription"));
			LiteProfile liteProfile = new LiteProfile(rs.getString("origin"), rs.getString("name"), aboutMe, rs.getString("profilePicture"), rs.getString("profileType") != null ? LiteProfile.Type.valueOf(rs.getString("profileType")) : null, rs.getBigDecimal("salary"), rs.getString("email"), average, rs.getInt("likeQty"));

			return new Service(rs.getString("id"), rs.getString("title"), rs.getString("description"), rs.getDate("creationDate"), rs.getDate("lastUpdate")
							 , address, LiteProfile.Type.valueOf(rs.getString("candidateType")), serviceAverage, category, Service.Type.valueOf(rs.getString("type")), rs.getString("attachment"), null
							 , Service.ExperienceLevel.valueOf(rs.getString("experienceLevel")), rs.getInt("vacancies"), payment
							 , Service.Duration.valueOf(rs.getString("duration")), Service.Dedication.valueOf(rs.getString("dedication"))
					         , null, Status.valueOf(rs.getString("status")), liteProfile, null);
		} else {
			return null;
		}
	}
}
