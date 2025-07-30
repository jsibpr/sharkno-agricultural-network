package com.module.core.models.view;

import java.time.LocalDate;

import io.swagger.annotations.ApiModelProperty;

public class SegmentForView {
	
	private String id;
	private String position;
	private String company;
	@ApiModelProperty(example = "2019-05-29")
	private LocalDate startDate;
	@ApiModelProperty(example = "2019-05-29")
	private LocalDate endDate;
	private Integer addressId;
	private String description;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	public LocalDate getStartDate() {
		return startDate;
	}
	
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate;
	}
	
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	public Integer getAddressId() {
		return addressId;
	}
	
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
