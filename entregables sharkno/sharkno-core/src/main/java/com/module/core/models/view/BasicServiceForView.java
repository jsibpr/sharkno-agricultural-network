package com.module.core.models.view;

import java.util.List;

import com.module.core.models.service.Service;
import com.module.core.models.service.Service.Dedication;
import com.module.core.models.service.Service.Duration;
import com.module.core.models.service.Service.ExperienceLevel;
import com.module.core.models.service.Service.Type;

public class BasicServiceForView {
	
	private String id;
	private String title;
	private String description;
	private Integer addressId;
	private String categoryId;
	private Service.Type type;
	private String attachment;
	private List<String> skillsIds;
	private ExperienceLevel experienceLevel;
	private Duration duration;
	private Dedication dedication;
	
	public BasicServiceForView(String id, String title, String description, Integer addressId, String categoryId,
			Type type, String attachment, List<String> skillsIds, ExperienceLevel experienceLevel, Duration duration,
			Dedication dedication) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.addressId = addressId;
		this.categoryId = categoryId;
		this.type = type;
		this.attachment = attachment;
		this.skillsIds = skillsIds;
		this.experienceLevel = experienceLevel;
		this.duration = duration;
		this.dedication = dedication;
	}

	public BasicServiceForView() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Service.Type getType() {
		return type;
	}

	public void setType(Service.Type type) {
		this.type = type;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public List<String> getSkillsIds() {
		return skillsIds;
	}

	public void setSkillsIds(List<String> skillsIds) {
		this.skillsIds = skillsIds;
	}

	public ExperienceLevel getExperienceLevel() {
		return experienceLevel;
	}

	public void setExperienceLevel(ExperienceLevel experienceLevel) {
		this.experienceLevel = experienceLevel;
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
	
}