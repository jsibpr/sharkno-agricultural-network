package com.module.core.dao.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.module.core.dao.mappers.ConversationMapper;
import com.module.core.dao.mappers.MessageMapper;
import com.module.core.models.Conversation;
import com.module.core.models.Message;

@Repository
public class MessageRepository {
	
	@Autowired
	private JdbcTemplate template;
	
	private static final String MESSAGE_TABLE = "messages";
	private static final String MESSAGE_FIELDS = "id, text, creationDate, originId, destinationId, readMark";
	private static final String PROFILE_TABLE = "profiles";
	private static final String JOIN_FIELDS = "PRO.id pro_id, name, profilePicture, email, type, salary, averageReview, likeQty";

	public List<Message> getMessages(String idUser, String idProfile) {
			return template.query("select * from " + MESSAGE_TABLE + " where (originId = ? and destinationId = ?) or (destinationId = ? and originId = ?) order by creationDate", new Object[] {idUser, idProfile, idUser, idProfile}, new MessageMapper());
	}
	
	public Message getLastMessage (String idUser, String idProfile){
		try {
			return template.queryForObject("select * from " + MESSAGE_TABLE + " where (originId = ? and destinationId = ?) or (destinationId = ? and originId = ?) order by creationDate DESC limit 1", new Object[] {idUser, idProfile, idUser, idProfile}, new MessageMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Conversation> getConversationProfiles (String idUser){
		return template.query("select " + JOIN_FIELDS + " from " + PROFILE_TABLE + " PRO where id = ANY (select distinct case when originId = ? then destinationId when destinationId = ? then originId end from " + MESSAGE_TABLE + ")", new Object[] {idUser, idUser}, new ConversationMapper());
	}

	public void addMessage(Message message) {
		template.update("insert into " + MESSAGE_TABLE + " (" + MESSAGE_FIELDS + ") values (?, ?, ?, ?, ?, ?)", message.getId(), message.getText(), message.getCreationDate(), message.getOriginId(), message.getDestinationId(), message.isReadMark()? 1 : 0);
	}

	public void markAsRead(List<Message> messages) {
		messages.forEach(m -> template.update("update " + MESSAGE_TABLE + " set readMark = ? where id = ?", 1, m.getId() ));
	}

}
