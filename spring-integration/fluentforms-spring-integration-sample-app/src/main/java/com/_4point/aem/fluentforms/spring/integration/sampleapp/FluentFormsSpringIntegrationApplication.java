package com._4point.aem.fluentforms.spring.integration.sampleapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FluentFormsSpringIntegrationApplication {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsSpringIntegrationApplication.class);

	public FluentFormsSpringIntegrationApplication(GitConfig gitConfig) {
    	gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringIntegrationApplication.class, args);
	}

}
