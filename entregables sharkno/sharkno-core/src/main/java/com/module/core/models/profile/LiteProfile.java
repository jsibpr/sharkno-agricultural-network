package com.module.core.models.profile;

import java.math.BigDecimal;

public class LiteProfile {
	
	public enum Type {
		BUSINESS,
		TALENT,
		DUAL
	}
	
	private String id;
	private String name;
	private AboutMe aboutMe;
	private String profilePicture;
	private Type type;
	private BigDecimal salary;
	private String email;
	private Float averageReview;
	private int likeQty;
	
	public LiteProfile(String id, String name, AboutMe aboutMe, String profilePicture, Type type, BigDecimal salary, String email, Float averageReview, int likeQty) {
		this.id = id;
		this.name = name;
		this.aboutMe = aboutMe;
		this.profilePicture = profilePicture;
		this.type = type;
		this.salary = salary;
		this.email = email;
		this.averageReview = averageReview;
		this.likeQty = likeQty;
	}
	
	public LiteProfile() {
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProfilePicture() {
		return profilePicture;
	}
	
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public AboutMe getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(AboutMe aboutMe) {
		this.aboutMe = aboutMe;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public Float getAverageReview() {
		return averageReview;
	}

	public void setAverageReview(Float averageReview) {
		this.averageReview = averageReview;
	}

	public int getLikeQty() {
		return likeQty;
	}

	public void setLikeQty(int likeQty) {
		this.likeQty = likeQty;
	}
	
}
