package com._4point.aem.fluentforms.spring.integration.sampleapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.dsl.HttpRequestHandlerEndpointSpec;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;

import net.sf.saxon.TransformerFactoryImpl;

@SpringBootApplication
public class FluentFormsSpringIntegrationApplication {
	private final static Logger log = LoggerFactory.getLogger(FluentFormsSpringIntegrationApplication.class);

	private static final String XSLT = 
			"""
			<?xml version="1.0" encoding="UTF-8"?>
			
			<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<xsl:mode on-no-match="shallow-copy"/>
			<xsl:template match="/root/payload">
				<payload>Some replaced text.</payload>
			</xsl:template>
			
			</xsl:stylesheet>
			""";

	private static final Resource XSLT_RESOURCE = new ByteArrayResource(XSLT.getBytes(StandardCharsets.UTF_8)); 

	public FluentFormsSpringIntegrationApplication(GitConfig gitConfig) {
		gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringIntegrationApplication.class, args);
	}

	@Bean
	IntegrationFlow flow() {
		return IntegrationFlow.from(httpMessageSource())
				.transform(createXsltTransformer(XSLT_RESOURCE))
				.handle(handler())
				.get();
	}

	private XsltPayloadTransformer createXsltTransformer(Resource resource) {
		TransformerFactory transformerFactory = new TransformerFactoryImpl();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "file");
        try {
			StreamSource source = new StreamSource(resource.getInputStream());
			Templates templates = transformerFactory.newTemplates(source);
			return new XsltPayloadTransformer(templates);
		} catch (TransformerConfigurationException | IOException e) {
			throw new MessagingException("Error when creating XsltTransformer.", e);
		}
	}

	private GenericHandler<String> handler() {
		return (GenericHandler<String>) (payload, headers) -> {
			FluentFormsSpringIntegrationApplication.log.atInfo().addArgument(payload).log("Found String '{}'.");
			return "Response '" + payload + "'.";
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
