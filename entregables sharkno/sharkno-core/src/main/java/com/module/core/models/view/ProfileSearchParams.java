package com.module.core.models.view;

import java.math.BigDecimal;

import com.module.core.models.profile.LiteProfile;

public class ProfileSearchParams {
	
	private String fragment;
	private Integer addressId;
	private BigDecimal salaryFrom;
	private BigDecimal salaryTo;
	private LiteProfile.Type type;
	private Integer limit;
	private String skillId;
	private Boolean isContact;
	
	public String getFragment() {
		return fragment;
	}
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public BigDecimal getSalaryFrom() {
		return salaryFrom;
	}
	public void setSalaryFrom(BigDecimal salaryFrom) {
		this.salaryFrom = salaryFrom;
	}
	public BigDecimal getSalaryTo() {
		return salaryTo;
	}
	public void setSalaryTo(BigDecimal salaryTo) {
		this.salaryTo = salaryTo;
	}
	public LiteProfile.Type getType() {
		return type;
	}
	public void setType(LiteProfile.Type type) {
		this.type = type;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public String getSkillId() {
		return skillId;
	}
	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
	public Boolean getIsContact() {
		return isContact;
	}
	public void setIsContact(Boolean isContact) {
		this.isContact = isContact;
	}

}
