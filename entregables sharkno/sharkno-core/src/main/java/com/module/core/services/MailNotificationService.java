package com.module.core.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.core.clients.MailClient;
import com.module.core.models.Mail;
import com.module.core.models.Notification;

@Service
public class MailNotificationService {
	
	@Autowired
	MailClient mailClient;
	
	@Autowired
	ProfileService profileService;
	
	public void notificationMail(Notification notification){
		String id = UUID.randomUUID().toString().substring(0,18);
		String destinationMail = profileService.getEmail(notification.getUserId());
		String message = notification.getText();
		String subject = "Nueva notificación";
		Mail mail = new Mail(id, subject, destinationMail, message);
		mailClient.sendMail(mail);
	}

}
