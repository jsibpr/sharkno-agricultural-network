package com.module.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.module.core.clients.FilesClient;
import com.module.core.exceptions.SharknoException;
import com.module.core.models.Session;

@Service
public class ImageService {
	
	@Autowired
	FilesClient filesClient;
	
	@Autowired
	ProfileService profileService;
	
	@Autowired
	ProductService productService;

	public void uploadProfileImage (MultipartFile multipartFile, Session session) throws SharknoException {
		String imageUrl = filesClient.uploadFile(multipartFile);
		profileService.updateProfileImage(imageUrl, session.getUser().getId());	
	}
	
	public void uploadProductImage (MultipartFile multipartFile, String productId, Session session) throws SharknoException {
		String imageUrl = filesClient.uploadFile(multipartFile);
		productService.updateProductImage(imageUrl, productId, session);
	}

}
