package com.module.files.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.module.files.exceptions.FilesException;
import com.module.files.exceptions.NotFoundException;
import com.module.files.exceptions.RepositoryException;

@Service
public class FilesService {
	
	private static Logger LOG = LoggerFactory.getLogger(FilesService.class);
	
	@Value("${file.region}")
	private Regions region;
	
	@Value("${file.bucket}")
	private String bucket;
	
	@Value("${file.cdn}")
	private String cdn;

	public String uploadFile (MultipartFile multipartFile) throws FilesException {
		//Null file returns a 404 not found exception
		if (multipartFile == null) {
			throw new NotFoundException();
		}
		
		File file = null;
		try {
			file = multipartToFile(multipartFile);
		} catch (IOException e) {
			//Error converting multipartFile into File
			throw new NotFoundException();
		}
	
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(region).build();
        try {
        	s3.putObject(bucket, file.getName(), file);
        } catch (AmazonServiceException e) {
        	//Any error during the communication with AWS will return a 503 status code (service unavailable)
        	LOG.error("Error while connecting with AWS", e);
            throw new RepositoryException(e);
        }
        LOG.info("Uploaded file {} in bucket {} located in region {}", file.getName(), bucket, region);
        return cdn.concat(file.getName());
	}
	
	public static File multipartToFile(MultipartFile multipart) throws IOException {
	    File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + multipart.getOriginalFilename());
	    multipart.transferTo(tempFile);
	    return tempFile;
	}
}
