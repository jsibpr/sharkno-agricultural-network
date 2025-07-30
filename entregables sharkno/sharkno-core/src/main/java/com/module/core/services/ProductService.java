package com.module.core.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module.core.dao.repositories.PaymentRepository;
import com.module.core.dao.repositories.ProductRepository;
import com.module.core.dao.repositories.ServiceRepository;
import com.module.core.exceptions.ForbiddenException;
import com.module.core.exceptions.NotFoundException;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.Entity;
import com.module.core.models.Session;
import com.module.core.models.product.Product;
import com.module.core.models.profile.LiteProfile;
import com.module.core.models.view.ProductForView;

@Service
@Transactional
public class ProductService {

	Logger log = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	ServiceRepository serviceRepository;
	
	public String createProduct (ProductForView productForView, String originId) {
		Product product = new Product();
		String id = UUID.randomUUID().toString().substring(0,18);
		product.setId(id);
		product.setTitle(productForView.getTitle());
		product.setDescription(productForView.getDescription());
		product.setCreationDate(new Date());
		product.setLastUpdate(new Date ());
		product.setCategory(new Entity(productForView.getCategory(), null));
		product.setType(productForView.getType());
		product.setAttachment(productForView.getAttachment());
		product.setQuantity(productForView.getQuantity());
		product.setStatus(Product.Status.OPEN);
		product.setImage(productForView.getImage());
		LiteProfile origin = new LiteProfile();
		origin.setId(originId);
		product.setOrigin(origin);
		product.setUrl(productForView.getUrl());
		productRepository.insertProduct(product);
		paymentRepository.createPayment(product.getId(), productForView.getPayment());
		log.info("Product with id={} created",id);
		return id;
	}
	
	public void updateProduct (ProductForView productForView) {
		Product product = new Product();
		product.setId(productForView.getId());
		product.setTitle(productForView.getTitle());
		product.setDescription(productForView.getDescription());
		product.setLastUpdate(new Date ());
		product.setCategory(new Entity(productForView.getCategory(), null));
		product.setType(productForView.getType());
		product.setAttachment(productForView.getAttachment());
		product.setQuantity(productForView.getQuantity());
		product.setStatus(Product.Status.OPEN);
		product.setUrl(productForView.getUrl());
		product.setImage(productForView.getImage());
		productRepository.updateProduct(product);
		paymentRepository.updatePayment(product.getId(), productForView.getPayment());
	}
	
	public void deleteProduct (String productId) {
		List<String> services = serviceRepository.getServicesFromProduct(productId);
		services.forEach((String serviceId) -> serviceRepository.deleteProductFromService(productId, serviceId));
		paymentRepository.deletePayment(productId);
		productRepository.deleteProduct(productId);
	}
	
	public Product getProduct (String productId) throws NotFoundException {
		Product product = productRepository.getProduct(productId);
		if (product == null ) {
			throw new NotFoundException();
		}
		product.setServices(getServicesFromProduct(productId));
		return product;
	}
	
	public void updateProductStatus (String productId, Product.Status status) {
		productRepository.updateProductStatus(productId, status);
	}
	
	public List<Product> searchProducts (String originId, String titleFragment) {
		return productRepository.searchProducts(originId, titleFragment);
	}
	
	public List<com.module.core.models.service.Service> getServicesFromProduct(String productId){
		List<String>servicesIds = serviceRepository.getServicesFromProduct(productId);
		List<com.module.core.models.service.Service>services = new ArrayList<>();
		servicesIds.forEach((String serviceId) -> services.add(serviceRepository.getService(serviceId)));
		return services;
	}
	
	public void updateProductImage (String imageUrl, String productId, Session session) throws SharknoException {
		Product product = productRepository.getProduct(productId);
		if (product == null) {
			// Product does not exist in database
			throw new NotFoundException();
		}
		if (!session.getUser().getId().equals(product.getOrigin().getId())) {
			// User is trying to update a product which he is not the owner
			throw new ForbiddenException();
		}
		productRepository.updateProductImage(imageUrl, productId);
	}
	

}
