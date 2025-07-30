package com.module.core.models.service;

import java.util.Date;
import java.util.List;

import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.product.Product;
import com.module.core.models.profile.LiteProfile;

public class Service extends BasicService{
	
	public enum Type {
		ONE_TIME,
		ONGOING,
		COMPLEX
	}
	
	public enum ExperienceLevel {
		ENTRY,
		INTERMEDIATE,
		EXPERT
	}
	
	public enum Dedication {
		LESS30,
		MORE30,
		NA
	}
	
	public enum Duration {
		SHORT,
		MEDIUM,
		LONG
	}

	private Entity category;
	private Type type;
	private String attachment;
	private List<Entity> skills;
	private ExperienceLevel experienceLevel;
	private int vacancies;
	private Payment payment;
	private Duration duration;
	private Dedication dedication;
	private List<Candidate> candidates;
	private LiteProfile origin;
	private List<Product> products;
	
	public Service() {
		super();
	}
	
	public Service(String id, String title, String description, Date creationDate, Date lastUpdate, Address address, LiteProfile.Type candidateType, Float averageServiceReview,
			Entity category, Type type, String attachment, List<Entity> skills, ExperienceLevel experienceLevel,
			int vacancies, Payment payment, Duration duration, Dedication dedication,
			List<Candidate> candidates, Status status, LiteProfile origin, List<Product> products) {
		super(id, title, description, creationDate, lastUpdate, address, status, candidateType, averageServiceReview);
		this.category = category;
		this.type = type;
		this.attachment = attachment;
		this.skills = skills;
		this.experienceLevel = experienceLevel;
		this.vacancies = vacancies;
		this.payment = payment;
		this.duration = duration;
		this.dedication = dedication;
		this.candidates = candidates;
		this.origin = origin;
		this.products = products;
	}

	public Entity getCategory() {
		return category;
	}

	public void setCategory(Entity category) {
		this.category = category;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public List<Entity> getSkills() {
		return skills;
	}

	public void setSkills(List<Entity> skills) {
		this.skills = skills;
	}

	public ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public int getVacancies() {
		return vacancies;
	}

	public void setVacancies(int vacancies) {
		this.vacancies = vacancies;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Dedication getDedication() {
		return dedication;
	}

	public void setDedication(Dedication dedication) {
		this.dedication = dedication;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
