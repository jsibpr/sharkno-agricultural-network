package com.module.core.models.view;

import com.module.core.models.product.Product;
import com.module.core.models.product.Product.Type;
import com.module.core.models.service.Payment;

public class ProductForView {

	private String id;
	private String attachment;
	private String category;
	private Payment payment;
	private int quantity;
	private String title;
	private String description;
	private Product.Type type;
	private String url;
	private String image;
	
	public ProductForView() {
	}

	public ProductForView(String id, String attachment, String category, Payment payment, int quantity, String title,
			String description, Type type, String url, String image) {
		this.id = id;
		this.attachment = attachment;
		this.category = category;
		this.payment = payment;
		this.quantity = quantity;
		this.title = title;
		this.description = description;
		this.type = type;
		this.url = url;
		this.image = image;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
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

	public Product.Type getType() {
		return type;
	}

	public void setType(Product.Type type) {
		this.type = type;
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
	
}
