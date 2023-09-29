package com._4point.aem.fluentforms.spring.integration.sampleapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class FluentFormsSpringIntegrationApplication {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsSpringIntegrationApplication.class);

	public FluentFormsSpringIntegrationApplication(GitConfig gitConfig) {
		gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringIntegrationApplication.class, args);
	}

	@Bean
	IntegrationFlow flow() {
		return IntegrationFlow.from((MessageSource<String>) () -> MessageBuilder.withPayload("testString").build())
				.handle((GenericHandler<String>) (payload, headers) -> {
					FluentFormsSpringIntegrationApplication.log.atInfo().addArgument(payload).log("Found String '{}'.");
					return null;
				}).get();
	}

}
