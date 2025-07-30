package com.module.core.models.view;

import java.util.List;

import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.BasicService.Status;
import com.module.core.models.service.Payment;
import com.module.core.models.service.Service.Dedication;
import com.module.core.models.service.Service.Duration;
import com.module.core.models.service.Service.ExperienceLevel;
import com.module.core.models.service.Service.Type;

public class ServiceForView extends BasicServiceForView{
	
	private Status status;
	private int vacancies;
	private Payment payment;
	private LiteProfile.Type candidateType;
	
	public ServiceForView() {
		
	}

	public ServiceForView(String id, String title, String description, Integer addressId, String categoryId, Type type,
			String attachment, List<String> skillsIds, ExperienceLevel experienceLevel, Duration duration,
			Dedication dedication, Status status, int vacancies, Payment payment,
			com.module.core.models.profile.LiteProfile.Type candidateType) {
		super(id, title, description, addressId, categoryId, type, attachment, skillsIds, experienceLevel, duration,
				dedication);
		this.status = status;
		this.vacancies = vacancies;
		this.payment = payment;
		this.candidateType = candidateType;
	}

	public ServiceForView(String id, String title, String description, Integer addressId, String categoryId, Type type,
			String attachment, List<String> skillsIds, ExperienceLevel experienceLevel, Duration duration,
			Dedication dedication) {
		super(id, title, description, addressId, categoryId, type, attachment, skillsIds, experienceLevel, duration,
				dedication);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getVacancies() {
		return vacancies;
	}

	public void setVacancies(int vacancies) {
		this.vacancies = vacancies;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public LiteProfile.Type getCandidateType() {
		return candidateType;
	}

	public void setCandidateType(LiteProfile.Type candidateType) {
		this.candidateType = candidateType;
	}
		
}
