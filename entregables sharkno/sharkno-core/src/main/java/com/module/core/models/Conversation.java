package com.module.core.models;

import com.module.core.models.profile.LiteProfile;

public class Conversation {
	
	private LiteProfile profile;
	private Message lastMessage;
	
	public Conversation() {
	}
	
	public Conversation(LiteProfile person, Message lastMessage) {
		this.profile = person;
		this.lastMessage = lastMessage;
	}
	
	public LiteProfile getProfile() {
		return profile;
	}
	
	public void setProfile(LiteProfile profile) {
		this.profile = profile;
	}

	public Message getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}
	
	

}
