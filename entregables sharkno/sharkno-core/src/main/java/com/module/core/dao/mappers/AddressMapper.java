package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Address;

public class AddressMapper implements RowMapper<Address> {

	@Override
	public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			return new Address(rs.getInt("id"), rs.getString("name"), rs.getString("countryCode"));
		} else {
			return null;
		}
	}

}
