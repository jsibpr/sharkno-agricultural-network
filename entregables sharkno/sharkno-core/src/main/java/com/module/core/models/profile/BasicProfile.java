package com.module.core.models.profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.module.core.models.Address;
import com.module.core.models.Entity;

import io.swagger.annotations.ApiModelProperty;

public class BasicProfile extends LiteProfile{

	@ApiModelProperty(example = "2019-05-29")
	private LocalDate birthDate;
	private String phone;
	private Address address;
	private String web;
	private List<Entity> skills;
	
	public BasicProfile() {
	}
	
	public BasicProfile(String id, String name, AboutMe aboutMe, String profilePicture, LocalDate birthDate,
			String email, String phone, Address address, String web, List<Entity> skills, Type type, BigDecimal salary, Float averageReview,
			int likeQty) {
		super(id, name, aboutMe, profilePicture, type, salary, email, averageReview, likeQty);
		this.birthDate = birthDate;
		this.phone = phone;
		this.address = address;
		this.web = web;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public List<Entity> getSkills() {
		return skills;
	}

	public void setSkills(List<Entity> skills) {
		this.skills = skills;
	}

}
