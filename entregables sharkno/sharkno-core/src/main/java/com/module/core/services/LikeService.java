package com.module.core.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.LikeRepository;
import com.module.core.dao.repositories.TopicRepository;
import com.module.core.models.topic.TopicResponse;

@Service
@Transactional
public class LikeService {

	@Autowired
	LikeRepository likeRepository;
	
	@Autowired
	TopicRepository topicRepository;
	
	public void createLike(String originId, String destination, String type) {
		TopicResponse topicResponse = topicRepository.getTopicResponse(destination);
		likeRepository.createLike(originId, destination, type, new Date());
		likeRepository.updateCountLikeProfile(topicResponse.getOrigin().getId());
	}
	
	public void deleteLike(String originId, String destination) {
		TopicResponse topicResponse = topicRepository.getTopicResponse(destination);
		likeRepository.deleteLike(originId, destination);
		likeRepository.updateCountLikeProfile(topicResponse.getOrigin().getId());
	}
	
}
