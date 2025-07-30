package com.module.mail;

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

    public static final String MAIL_CONTROLLER_TAG = "MailController";
	
    @Bean
    public Docket apiDocket() {
    	ArrayList<SecurityScheme> auth = new ArrayList<>(1);
		auth.add(new ApiKey("basicAuth", "Authorization", "header"));
        return new Docket(DocumentationType.SWAGGER_2)
        		.securitySchemes(auth)
                .apiInfo(apiInfo())
                .tags(new Tag(MAIL_CONTROLLER_TAG, ""))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.module.mail.controllers"))
                .paths(PathSelectors.any() )
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Mail Service",
                "CRUD operations for Mail objects. Also a method to send emails with scheduled retries is available.\n" +
                        "All end-points are protected with basic authentication. Provide an HTTP Header with name 'Authorization' alongside your API Key.",
                "1.2",
                "Terms of service",
                new Contact("Nettrim", "http://www.nettrim.es", "info@nettrim.es"),
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
      
}