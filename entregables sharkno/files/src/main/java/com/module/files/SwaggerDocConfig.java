package com.module.files;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerDocConfig {

    public static final String FILES_CONTROLLER_TAG = "FilesController";
	
    @Bean
    public Docket apiDocket() {
    	ArrayList<SecurityScheme> auth = new ArrayList<>(1);
		auth.add(new ApiKey("basicAuth", "Authorization", "header"));
        return new Docket(DocumentationType.SWAGGER_2)
        		.securitySchemes(auth)
                .apiInfo(apiInfo())
                .tags(new Tag(FILES_CONTROLLER_TAG, "Files Controller"))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.module.files.controllers"))
                .paths(PathSelectors.any() )
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Files Service",
                "Operations to upload files into a S3 bucket and retrieve the route to that file.\n" +
                        "All end-points are protected with basic authentication. Provide an HTTP Header with name 'Authorization' alongside your API Key.",
                "1.0",
                "Terms of service",
                new Contact("Nettrim", "http://www.nettrim.es", "info@nettrim.es"),
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
      
}