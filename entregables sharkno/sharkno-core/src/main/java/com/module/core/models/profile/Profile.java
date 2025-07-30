package com.module.core.models.profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.Segment;
import com.module.core.models.Recommendation;
import com.module.core.models.Review;

public class Profile extends BasicProfile{

	private List<Segment> experience;
	private List<Segment> studies;
	private List<Review> reviews;
	private List<Recommendation> recommendations;
	private List<LiteProfile> contacts;
	
	public Profile(String id, String name, AboutMe aboutMe, String profilePicture, LocalDate birthDate, String email,
			String phone, Address address, String web, List<Entity> skills, List<Segment> experience, List<Segment> studies, 
			List<Review> reviews, List<LiteProfile> contacts, List<Recommendation> recommendations, Type type, BigDecimal salary,
			Float averageReview, int likeQty) {
		super(id, name, aboutMe, profilePicture, birthDate, email, phone, address, web, skills, type, salary, averageReview,
				likeQty);
		this.experience = experience;
		this.studies = studies;
		this.reviews = reviews;
		this.contacts = contacts;
		this.recommendations = recommendations;
	}

	public Profile() {
	}

	public List<Segment> getExperience() {
		return experience;
	}

	public void setExperience(List<Segment> experience) {
		this.experience = experience;
	}

	public List<Segment> getStudies() {
		return studies;
	}

	public void setStudies(List<Segment> studies) {
		this.studies = studies;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<LiteProfile> getContacts() {
		return contacts;
	}

	public void setContacts(List<LiteProfile> contacts) {
		this.contacts = contacts;
	}

	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<Recommendation> recommendations) {
		this.recommendations = recommendations;
	}
	
}
