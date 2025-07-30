package com.module.core.services;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.clients.NotificationClient;
import com.module.core.dao.repositories.ContactRepository;
import com.module.core.exceptions.NotFoundException;
import com.module.core.models.Contact;
import com.module.core.models.Contact.Status;
import com.module.core.models.Notification;
import com.module.core.models.Session;

@Service
@Transactional
public class ContactService {
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	NotificationClient notificationClient;
	
	@Autowired
	MailNotificationService mailNotificationService;
	
	@Value("${mail.send}")
	private boolean sendEmail;


	public void addContact(Session session, String contactDestination) {
		if(contactStatus(session.getUser().getId(), contactDestination) == null) {
			String id = UUID.randomUUID().toString().substring(0,18);
			Contact fillContact = new Contact(id, LocalDate.now(), session.getUser().getId(), contactDestination, Status.PENDING);
			contactRepository.insert(fillContact);
			Notification notification = new Notification(UUID.randomUUID().toString().substring(0,18), contactDestination, "Nueva petición de contacto", new Date(), null, "CONTACT", session.getUser().getId());
			notificationClient.createNotification(notification);
			if(sendEmail) {
				mailNotificationService.notificationMail(notification);
			}
		}
	}

	public void deleteContact(String userId, String profileId) throws NotFoundException {
		Contact contact = contactRepository.getContact(userId, profileId);
		if(contact == null) {
			contact = contactRepository.getContact(profileId, userId);
		}
		if (contact != null) {
			contactRepository.delete(contact.getId());
		}else {
			throw new NotFoundException();
		}
	}

	public void confirmContact(String userId, String profileId) throws NotFoundException {
		Contact contact = contactRepository.getContact(profileId, userId);
		if (contact == null ) {
			throw new NotFoundException();
		}
		if (contact.getStatus()==Contact.Status.PENDING) {
			contactRepository.updateContactStatus(contact.getId(), Contact.Status.CONFIRMED);
		}
	}

	public void rejectContact(String userId, String profileId) throws NotFoundException {
		Contact contact = contactRepository.getContact(profileId, userId);
		if (contact == null ) {
			throw new NotFoundException();
		}
		if (contact.getStatus()==Contact.Status.PENDING) {
			contactRepository.updateContactStatus(contact.getId(), Contact.Status.REJECTED);
		}

	}

	public Status contactStatus(String idUser, String profileId) {
		Contact contact = contactRepository.getContact(idUser, profileId);
		if(contact != null) {
			return contact.getStatus();
		}
		contact = contactRepository.getContact(profileId, idUser);
		if(contact != null) {
			return contact.getStatus();
		}
		return null;
	}

	public List<Contact> getAll(String userId) {
		return contactRepository.getAll(userId);
	}
	
	
}
