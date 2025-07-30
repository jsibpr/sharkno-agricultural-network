package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Entity;

public class EntityMapper implements RowMapper<Entity>{

	@Override
	public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			return new Entity(rs.getString("id"), rs.getString("name"));
		} else {
			return null;
		}
	}

}
