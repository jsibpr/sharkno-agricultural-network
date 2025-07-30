package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.ContactMapper;
import com.module.core.models.Contact;

@Repository
public class ContactRepository {
	
	@Autowired
	private JdbcTemplate template;
	
	private static final String CONTACTS_TABLE = "contacts";
	private static final String CONTACTS_FIELDS = "id, creationDate, origin, destination, status";

	public void insert(Contact contact) {
		template.update("insert into " + CONTACTS_TABLE + "(" + CONTACTS_FIELDS + ") values (?, ?, ?, ?, ?)", contact.getId(), contact.getCreationDate(), contact.getOrigin(), contact.getDestination(), contact.getStatus().toString());
	}
	
	public Contact getContact(String contactId) {
		try {
			return template.queryForObject("select " + CONTACTS_FIELDS + " from " + CONTACTS_TABLE + " where id = ?", new Object[] {contactId}, new ContactMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public Contact getContact(String id1, String id2) {
		try {
			return template.queryForObject("select " + CONTACTS_FIELDS + " from " + CONTACTS_TABLE + " where origin = ? AND destination = ? ", new Object[] {id1, id2}, new ContactMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public void delete(String contactId) {
		template.update("delete from " + CONTACTS_TABLE + " where id = ?", contactId);
	}
	
	public void updateContactStatus (String contactId, Contact.Status status) {
		template.update("UPDATE " + CONTACTS_TABLE + " SET status = ? WHERE id = ?", status.toString(), contactId);
	}

	public List<Contact> getAll(String userId) {
		return template.query("select * from " + CONTACTS_TABLE + " where origin = ? or destination = ?" + " order by creationDate desc", new Object[] {userId, userId}, new ContactMapper());
	}

}
