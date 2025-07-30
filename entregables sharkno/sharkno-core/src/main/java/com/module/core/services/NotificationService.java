
package com.module.core.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.module.core.clients.NotificationClient;
import com.module.core.models.Notification;

@Service
public class NotificationService {

	@Autowired
	NotificationClient notificationClient;

	public List<Notification> userNotifications(String userId) {
		return notificationClient.getUserNotifications(userId);
	}

	public Integer userNumberNotification(String userId) {
		return notificationClient.getNumberNotifications(userId);
	}

	public void delete(String notificationId) {
		notificationClient.deleteNotification(notificationId);
	}

}