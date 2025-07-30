package com.module.mail.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.mail.models.Mail;
import com.module.mail.models.Mail.Status;

@Repository
public class MailRepository{
	
	@Autowired
	private JdbcTemplate template;
	
	private static final String MAIL_TABLE = "mail";
	private static final String MAIL_FIELDS = "id, subject, destinationMail, message, status, attemptsCount";
	private static final String MAIL_UPDATE_FIELDS = "subject = ?, destinationMail = ?, message= ?";
	
	public String insert(Mail mail, Status status, int attemptsCount) {
		String insertId = generateUuid();
		template.update("insert into " + MAIL_TABLE + "(" + MAIL_FIELDS + ") values (?, ?, ?, ?, ?, ?)",
			insertId, mail.getSubject(), mail.getDestinationMail(), mail.getMessage(), status.toString(), attemptsCount);
		return insertId;
	}
	
	public void delete(String mailId) {
		template.update("delete from " + MAIL_TABLE + " where id = ?", mailId);
	}

	public void update(Mail mail) {
		template.update("UPDATE " + MAIL_TABLE + " set " + MAIL_UPDATE_FIELDS + " where id = ?",
			mail.getSubject(), mail.getDestinationMail(), mail.getMessage(), mail.getId());
	}

	public List<Mail> getMails() {
		return template.query("select * from " + MAIL_TABLE, new MailMapper());
	}

	public Mail get(String mailId) {
		try {
			return template.queryForObject("select * from " + MAIL_TABLE + " where id = ?", new Object[] {mailId}, new MailMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Mail> getPendingMailsIds(int maxAttempts) {
		return template.query("select * from " + MAIL_TABLE + " where status = ? and attemptsCount <= ? ", new Object[] {Mail.Status.PENDING.toString(), maxAttempts}, new MailMapper());
	}
	
	public void updateStatus(String mailId) {
		template.update("UPDATE " + MAIL_TABLE + " set status = ? , attemptsCount = attemptsCount + 1 where id = ?", Mail.Status.SENT.toString(), mailId);
	}
	
	public void updateCount(String mailId) {
		template.update("UPDATE " + MAIL_TABLE + " set attemptsCount = attemptsCount + 1 where id = ?", mailId);
	}

	private String generateUuid() {
		return UUID.randomUUID().toString().substring(0,18);
	}
	
}

