package com.module.core.models.view;

import java.util.List;

import com.module.core.models.service.Service.Dedication;
import com.module.core.models.service.Service.Duration;
import com.module.core.models.service.Service.ExperienceLevel;
import com.module.core.models.service.Service.Type;

public class ExternalServiceForView extends BasicServiceForView{
	
	private String businessId;

	public ExternalServiceForView(String id, String title, String description, Integer addressId, String categoryId,
			Type type, String attachment, List<String> skillsIds, ExperienceLevel experienceLevel, Duration duration,
			Dedication dedication, String businessId) {
		super(id, title, description, addressId, categoryId, type, attachment, skillsIds, experienceLevel, duration,
				dedication);
		this.businessId = businessId;
	}

	public ExternalServiceForView() {
	}

	public ExternalServiceForView(String id, String title, String description, Integer addressId, String categoryId,
			Type type, String attachment, List<String> skillsIds, ExperienceLevel experienceLevel, Duration duration,
			Dedication dedication) {
		super(id, title, description, addressId, categoryId, type, attachment, skillsIds, experienceLevel, duration,
				dedication);
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	
}
