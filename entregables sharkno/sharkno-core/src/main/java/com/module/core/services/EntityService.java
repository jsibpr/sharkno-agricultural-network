package com.module.core.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.EntityRepository;
import com.module.core.models.Entity;

@Service
@Transactional
public class EntityService {
	
	@Autowired
	EntityRepository entityRepository;
	
	public String createEntity (String name, String type) {
		String id = UUID.randomUUID().toString().substring(0,18);
		Entity entity = new Entity(id, name);
		entityRepository.insertEntity(entity, type);
		return id;
	}
	
	public void deleteEntity (Entity entity) {
		entityRepository.deleteEntity(entity.getId());
	}
	
	public Entity getEntity (String id) {
		return entityRepository.getEntity(id);
	}
	
	public void updateEntity (Entity entity) {
		entityRepository.updateEntity(entity);
	}
	
	public void createEntityRelation (String relationId, String entityId, String use) {
		entityRepository.insertEntityRelation(entityId, relationId, use);
	}
	
	public void deleteEntityRelation (String relationId, String entityId) {
		entityRepository.deleteEntityRelation(relationId, entityId);
	}

	public List<Entity> getSkills(String id) {
		return entityRepository.getSkills(id);
	}
	
}
