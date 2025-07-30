package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Review;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;

public class ReviewMapper implements RowMapper<Review> {

	@Override
	public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			
			LiteProfile origin = new LiteProfile();
			origin.setId(rs.getString("rev_origin"));
			origin.setName(rs.getString("pro_name"));
			BasicService service = new BasicService();
			service.setId(rs.getString("rev_service"));
			service.setTitle(rs.getString("ser_title"));
			return new Review(rs.getString("rev_id"), 
					origin,
					rs.getString("rev_destination"), 
					service,
					rs.getString("rev_selfValuation"),
					rs.getString("rev_companyValuation"),
					rs.getDate("rev_creatDate"),
					rs.getInt("rev_skillValue"),
					rs.getInt("rev_commValue"),
					rs.getInt("rev_deadlineValue"),
					rs.getInt("rev_avaValue"),
					rs.getInt("rev_qualityValue"),
					rs.getInt("rev_coopValue"),
					Review.Type.valueOf(rs.getString("rev_type")));
		} else {
			return null;
		}
	}

}


