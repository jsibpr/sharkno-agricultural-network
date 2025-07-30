package com.module.mail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service

@Configuration
@EnableScheduling
public class ShedulerService {
	
	@Autowired 
	MailService mailService;

	@Scheduled(cron = "${mail.interval}")
	public void MailScheduling() {
		mailService.sendNotSent();
	}
}


