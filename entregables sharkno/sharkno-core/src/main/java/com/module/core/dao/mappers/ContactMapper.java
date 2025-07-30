package com.module.core.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.module.core.models.Contact;
import com.module.core.models.Contact.Status;

public class ContactMapper implements RowMapper<Contact> {
	
	@Override
	public Contact mapRow(ResultSet rs, int rowNum) throws SQLException{
		if (rs!= null) {
			return new Contact(rs.getString("id"),rs.getDate("creationDate").toLocalDate(), rs.getString("origin"), rs.getString("destination"), Status.valueOf(rs.getString("status")));	
		} else {
			return null;
		}
	}

}
