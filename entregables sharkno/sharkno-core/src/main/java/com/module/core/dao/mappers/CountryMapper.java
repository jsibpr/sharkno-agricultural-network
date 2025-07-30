package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Country;

public class CountryMapper implements RowMapper<Country> {

	@Override
	public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs!= null) {
			return new Country(rs.getString("ISO"), rs.getString("name"));
		} else {
			return null;
		}
	}

}
