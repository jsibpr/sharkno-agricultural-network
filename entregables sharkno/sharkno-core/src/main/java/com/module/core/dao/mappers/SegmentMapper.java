package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;
import com.module.core.models.Segment;

public class SegmentMapper implements RowMapper<Segment> {

	@Override
	public Segment mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			return new Segment(rs.getString("id"), rs.getString("position"), rs.getString("company")
				         , LocalDate.parse(rs.getString("startDate")), rs.getString("endDate") != null ? LocalDate.parse(rs.getString("endDate")) : null
				         , new Address(rs.getInt("address"),rs.getString("asciiname"),rs.getString("countryCode"))
				         , rs.getString("description"), Segment.Type.valueOf(rs.getString("type")));
		} else {
			return null;
		}
	}

}