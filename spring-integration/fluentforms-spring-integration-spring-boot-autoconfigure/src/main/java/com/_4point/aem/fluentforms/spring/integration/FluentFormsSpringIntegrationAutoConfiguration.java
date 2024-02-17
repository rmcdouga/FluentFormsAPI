package com._4point.aem.fluentforms.spring.integration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Lazy
@AutoConfiguration
@ConditionalOnWebApplication
public class FluentFormsSpringIntegrationAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AdaptiveFormsService integrationAdaptiveFormsService(com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService afService) {
		return new AdaptiveFormsService(afService);
	}

}
