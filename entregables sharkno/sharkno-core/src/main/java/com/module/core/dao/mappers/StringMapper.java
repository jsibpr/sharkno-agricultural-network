package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StringMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			return rs.getString("content");
		} else {
			return null;
		}
	}
}
