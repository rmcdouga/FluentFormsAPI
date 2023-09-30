package com._4point.aem.fluentforms.spring.integration.sampleapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.dsl.HttpRequestHandlerEndpointSpec;
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
		return IntegrationFlow.from(httpMessageSource())
				.handle(handler()).get();
	}

	private GenericHandler<String> handler() {
		return (GenericHandler<String>) (payload, headers) -> {
			FluentFormsSpringIntegrationApplication.log.atInfo().addArgument(payload).log("Found String '{}'.");
			return "Response";
		};
	}

	private HttpRequestHandlerEndpointSpec httpMessageSource() {
		return Http.inboundGateway("/service/test")
				   .requestMapping(r->r.methods(HttpMethod.POST))
				   ;
	}

	private MessageSource<String> stringMessageSource() {
		return (MessageSource<String>) () -> MessageBuilder.withPayload("testString").build();
	}

}
