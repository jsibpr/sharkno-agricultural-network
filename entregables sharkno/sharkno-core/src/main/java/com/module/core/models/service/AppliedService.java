package com.module.core.models.service;

import java.util.Date;
import java.util.List;

import com.module.core.models.Address;
import com.module.core.models.Entity;
import com.module.core.models.product.Product;
import com.module.core.models.profile.LiteProfile;

public class AppliedService extends Service{

	private boolean hasAlreadyApplied;
	
	public AppliedService() {
		super();
	}

	public AppliedService(String id, String title, String description, Date creationDate, Date lastUpdate, Address address, LiteProfile.Type candidateType,
			Float averageServiceReview, Entity category, Type type, String attachment, List<Entity> skills, ExperienceLevel experienceLevel, int vacancies,
			Payment payment, Duration duration, Dedication dedication, List<Candidate> candidates, Status status, LiteProfile origin, List<Product> products,
			boolean hasAlreadyApplied) {
		super(id, title, description, creationDate, lastUpdate, address, candidateType, averageServiceReview, category, type, attachment,
				skills, experienceLevel, vacancies, payment, duration, dedication, candidates, status, origin, products);
		this.hasAlreadyApplied = hasAlreadyApplied;
	}

	public boolean getHasAlreadyApplied() {
		return hasAlreadyApplied;
	}

	public void setHasAlreadyApplied(boolean hasAlreadyApplied) {
		this.hasAlreadyApplied = hasAlreadyApplied;
	}
}
