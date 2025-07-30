package com.module.core.models.product;

import java.util.Date;
import java.util.List;

import com.module.core.models.Entity;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.service.Payment;
import com.module.core.models.service.Service;

public class Product {
	
	public enum Type {
		EQUIPMENT,
		SOFTWARE,
		CONSUMABLES,
		MACHINERY
	}
	
	public enum Status {
		OPEN,
		CLOSED
	}

	private String id;
	private String title;
	private String description;
	private Date creationDate;
	private Date lastUpdate;
	private Entity category;
	private Type type;
	private String attachment;
	private int quantity;
	private Status status;
	private LiteProfile origin;
	private Payment payment;
	private String url;
	private String image; 
	private List<Service> services;
	
	public Product() {
	}

	public Product(String id, String title, String description, Date creationDate, Date lastUpdate, Entity category,
			Type type, String attachment, int quantity, Status status, LiteProfile origin, Payment payment, String url,
			String image, List<Service> services) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.category = category;
		this.type = type;
		this.attachment = attachment;
		this.quantity = quantity;
		this.status = status;
		this.origin = origin;
		this.payment = payment;
		this.url = url;
		this.image = image;
		this.services = services;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LiteProfile getOrigin() {
		return origin;
	}

	public void setOrigin(LiteProfile origin) {
		this.origin = origin;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
	
}
