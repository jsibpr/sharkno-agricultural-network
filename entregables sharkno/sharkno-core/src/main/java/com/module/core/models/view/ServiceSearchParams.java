package com.module.core.models.view;

import java.util.Date;

import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService;
import com.module.core.models.service.Payment;
import com.module.core.models.service.Service;

public class ServiceSearchParams {

	private String fragment;
	private String addressId;
	private String categoryId;
	private String originId;
	private String notOriginId;
	private Date creationDateFrom;
	private Date creationDateTo;
	private Service.Dedication dedication;
	private Service.Duration duration;
	private Service.ExperienceLevel experienceLevel;
	private Service.Type type;
	private BasicService.Status status;
	private Integer vacancies;
	private Integer limit;
	private LiteProfile.Type candidateType;
	private String skillId;
	private String candidateId;
	private String notCandidateId;
	private Payment.Type paymentType;

	public ServiceSearchParams() {
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public Service.Dedication getDedication() {
		return dedication;
	}

	public void setDedication(Service.Dedication dedication) {
		this.dedication = dedication;
	}

	public Service.Duration getDuration() {
		return duration;
	}

	public void setDuration(Service.Duration duration) {
		this.duration = duration;
	}

	public Service.ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(Service.ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public Service.Type getType() {
		return type;
	}

	public void setType(Service.Type type) {
		this.type = type;
	}

	public BasicService.Status getStatus() {
		return status;
	}

	public void setStatus(BasicService.Status status) {
		this.status = status;
	}

	public Integer getVacancies() {
		return vacancies;
	}

	public void setVacancies(Integer vacancies) {
		this.vacancies = vacancies;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public LiteProfile.Type getCandidateType() {
		return candidateType;
	}

	public void setCandidateType(LiteProfile.Type candidateType) {
		this.candidateType = candidateType;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public Payment.Type getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Payment.Type paymentType) {
		this.paymentType = paymentType;
	}

	public String getNotOriginId() {
		return notOriginId;
	}

	public void setNotOriginId(String notOriginId) {
		this.notOriginId = notOriginId;
	}

	public String getNotCandidateId() {
		return notCandidateId;
	}

	public void setNotCandidateId(String notCandidateId) {
		this.notCandidateId = notCandidateId;
	}

}
