package com.module.mail.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.module.mail.dao.MailRepository;
import com.module.mail.exceptions.NotFoundException;
import com.module.mail.models.Mail;

@Service
public class MailService {
	Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Value("${mail.attempt}")
	private Integer attempt;
	
	@Value("${mail.send}")
	private boolean sendEnable;
	
	@Autowired
    public JavaMailSender emailSender;
	
	@Autowired
	MailRepository mailRepository;
	
	@Autowired
	ShedulerService shedulerService;

	public void send(Mail mail) {
		if(!sendEnable) {
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(mail.getDestinationMail());
		message.setSubject(mail.getSubject());
		message.setText(mail.getMessage());
		try{
			emailSender.send(message);
		}catch(MailException mailException){
			log.error("An error occurred trying to send email with id {}",mail.getId(),mailException);
			mailRepository.updateCount(mail.getId());
			return;
		}

		log.info("Email with id={} sent successfully", mail.getId());
		mailRepository.updateStatus(mail.getId());
	}
		
	
	public void sendMail(Mail mail) {
		String mailId = mailRepository.insert(mail, Mail.Status.PENDING, 0);
		mail.setId(mailId);
		send(mail);
	}
	
	public void sendNotSent() {
		List<Mail> noSentMails = mailRepository.getPendingMailsIds(attempt);
		noSentMails.forEach(this::send);
	}

	public void deleteMail(String mailId) {
		mailRepository.delete(mailId);
	}

	public List<Mail> getMails() {
		return mailRepository.getMails();
	}
	
	public Mail get(String mailId) throws NotFoundException{
		Mail mail = mailRepository.get(mailId);
		if (mail == null) {
			throw new NotFoundException();
		}
		return mail;
	}

	public void updateMail(Mail mail) {
		mailRepository.update(mail);
	}

}
