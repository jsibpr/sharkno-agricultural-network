package com.module.core.models;

import java.util.Date;

import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;

public class Review {
	
	public enum Type {
		OWNER,
		EMPLOYEE
	}

	private String id;
	private LiteProfile origin;
	private String destination;
	private BasicService service;
	private String selfValuation;
	private String companyValuation;
	private Date creationDate;
	private Integer skillValue;
	private Integer communicationValue;
	private Integer deadlineValue;
	private Integer availabilityValue;
	private Integer qualityValue;
	private Integer cooperationValue;
	private Type type;
	
	public Review(String id, LiteProfile origin, String destination, BasicService service, String selfValuation,
			String companyValuation, Date creationDate, Integer skillValue, Integer communicationValue,
			Integer deadlineValue, Integer availabilityValue, Integer qualityValue, Integer cooperationValue,
			Type type) {
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.service = service;
		this.selfValuation = selfValuation;
		this.companyValuation = companyValuation;
		this.creationDate = creationDate;
		this.skillValue = skillValue;
		this.communicationValue = communicationValue;
		this.deadlineValue = deadlineValue;
		this.availabilityValue = availabilityValue;
		this.qualityValue = qualityValue;
		this.cooperationValue = cooperationValue;
		this.type = type;
	}

	public Review() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public BasicService getService() {
		return service;
	}

	public void setService(BasicService service) {
		this.service = service;
	}

	public String getSelfValuation() {
		return selfValuation;
	}

	public void setSelfValuation(String selfValuation) {
		this.selfValuation = selfValuation;
	}

	public String getCompanyValuation() {
		return companyValuation;
	}

	public void setCompanyValuation(String companyValuation) {
		this.companyValuation = companyValuation;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getSkillValue() {
		return skillValue;
	}

	public void setSkillValue(Integer skillValue) {
		this.skillValue = skillValue;
	}

	public Integer getCommunicationValue() {
		return communicationValue;
	}

	public void setCommunicationValue(Integer communicationValue) {
		this.communicationValue = communicationValue;
	}

	public Integer getDeadlineValue() {
		return deadlineValue;
	}

	public void setDeadlineValue(Integer deadlineValue) {
		this.deadlineValue = deadlineValue;
	}

	public Integer getAvailabilityValue() {
		return availabilityValue;
	}

	public void setAvailabilityValue(Integer availabilityValue) {
		this.availabilityValue = availabilityValue;
	}

	public Integer getQualityValue() {
		return qualityValue;
	}

	public void setQualityValue(Integer qualityValue) {
		this.qualityValue = qualityValue;
	}

	public Integer getCooperationValue() {
		return cooperationValue;
	}

	public void setCooperationValue(Integer cooperationValue) {
		this.cooperationValue = cooperationValue;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	

}
