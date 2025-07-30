package com.module.core.models.view;

import javax.validation.constraints.NotNull;

import com.module.core.models.Review.Type;

public class ReviewForView {
	
	private String destinationId;
	private String serviceId;
	@NotNull
	private String selfValuation;
	@NotNull
	private String companyValuation;
	@NotNull
	private Integer skillValue;
	@NotNull
	private Integer communicationValue;
	@NotNull
	private Integer deadlineValue;
	@NotNull
	private Integer availabilityValue;
	@NotNull
	private Integer qualityValue;
	@NotNull
	private Integer cooperationValue;
	private Type type;
	
	public ReviewForView(String destinationId, String serviceId, String selfValuation,
			String companyValuation, Integer skillValue, Integer communicationValue, Integer deadlineValue,
			Integer availabilityValue, Integer qualityValue, Integer cooperationValue, Type type) {
		this.destinationId = destinationId;
		this.serviceId = serviceId;
		this.selfValuation = selfValuation;
		this.companyValuation = companyValuation;
		this.skillValue = skillValue;
		this.communicationValue = communicationValue;
		this.deadlineValue = deadlineValue;
		this.availabilityValue = availabilityValue;
		this.qualityValue = qualityValue;
		this.cooperationValue = cooperationValue;
		this.type = type;
	}

	public ReviewForView() {
		
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
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