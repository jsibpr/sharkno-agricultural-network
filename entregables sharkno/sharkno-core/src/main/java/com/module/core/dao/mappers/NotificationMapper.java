package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Notification;

public class NotificationMapper implements RowMapper<Notification>{
	
	@Override
	public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
		if (rs != null) {
			return new Notification(rs.getString("id"), rs.getString("userId"), rs.getString("text"), rs.getDate("creationDate"), rs.getDate("readDate"), rs.getString("type"), rs.getString("originId"));
		} else {
			return null;
		}
	}
	
}