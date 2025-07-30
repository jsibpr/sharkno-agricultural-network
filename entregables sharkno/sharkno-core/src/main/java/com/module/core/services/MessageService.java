package com.module.core.services;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.MessageRepository;
import com.module.core.exceptions.NotFoundException;
import com.module.core.models.Conversation;
import com.module.core.models.Message;
import com.module.core.models.view.MessageForView;

@Service
@Transactional
public class MessageService {
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	ProfileService profileService;

	public List<Message> getMessages(String idUser, String idProfile) throws NotFoundException {
		List<Message> messages = messageRepository.getMessages(idUser, idProfile);
		if(messages == null) {
			throw new NotFoundException();
		}
		messageRepository.markAsRead(messages);
		return messages;
	}

	public List<Conversation> getConversations(String idUser) throws NotFoundException{
		List<Conversation> profiles = messageRepository.getConversationProfiles(idUser);
		if (profiles == null) {
			throw new NotFoundException();
		}
		for (Conversation conversation : profiles) {
			conversation.setLastMessage(messageRepository.getLastMessage(conversation.getProfile().getId(), idUser));
		}
		return profiles;
	}

	public void addMessage(MessageForView messageForView, String idUser) {
		String id = UUID.randomUUID().toString().substring(0,18);
		Date creationDate = new Date();
		messageRepository.addMessage(new Message(id, messageForView.getText(), creationDate, idUser, messageForView.getDestinationId(), false));
	}

}
