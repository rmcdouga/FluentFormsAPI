package com._4point.aem.fluentforms.spring.integration.sampleapp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.http.dsl.HttpRequestHandlerEndpointSpec;
import org.springframework.integration.xml.transformer.XPathHeaderEnricher;
import org.springframework.integration.xml.transformer.XsltPayloadTransformer;
import org.springframework.integration.xml.transformer.support.XPathExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MultiValueMap;

import com._4point.aem.fluentforms.spring.integration.AdaptiveFormsService;

import net.sf.saxon.TransformerFactoryImpl;

@SpringBootApplication
public class FluentFormsSpringIntegrationApplication {
	private static final String FORM_PARAM_NAME = "form";

	private final static Logger log = LoggerFactory.getLogger(FluentFormsSpringIntegrationApplication.class);

	private static final String XSLT = 
			"""
			<?xml version="1.0" encoding="UTF-8"?>
			
			<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
			<xsl:mode on-no-match="shallow-copy"/>
			<xsl:template match="/root/payload">
				<payload>Some replaced text.</payload>
				<headerData>
					<value1>headerValue</value1>
				</headerData>
			</xsl:template>
			
			</xsl:stylesheet>
			""";

	private static final Resource XSLT_RESOURCE = new ByteArrayResource(XSLT.getBytes(StandardCharsets.UTF_8)); 

	private final DeliveryService deliveryService = new DeliveryService();
	
	public FluentFormsSpringIntegrationApplication(GitConfig gitConfig) {
		gitConfig.logGitInformation();
	}

	public static void main(String[] args) {
		SpringApplication.run(FluentFormsSpringIntegrationApplication.class, args);
	}

	@Autowired
	AdaptiveFormsService myafService;
	
	@Bean
	IntegrationFlow postflow() {
		return IntegrationFlow.from(httpPostMessageSource())
				.transform(b->new String((byte[])b, StandardCharsets.UTF_8)) // Transform byte[] to String because DomSourceFactory does not take byte[]
				.log()
				.transform(createXsltTransformer(XSLT_RESOURCE))
				.enrichHeaders(FluentFormsSpringIntegrationApplication::addHeaderForLaterRetrieval)
				.transform(new XPathHeaderEnricher(of(Map.of(DeliveryService.Parameters.param1Key(), IncomingData.value1Xpath()))))
				.transform(deliveryService)
				.handle(writeOutPayload())
				.get();
	}

	@SuppressWarnings("unchecked")
	@Bean
	IntegrationFlow getflow() {
		return IntegrationFlow.from(httpGetMessageSource())
				.log()
//				.transform(mvm->"<html>" + ((MultiValueMap<String,String>)mvm).getFirst(FORM_PARAM_NAME) + "</html>")
				.transform(mvm->((MultiValueMap<String,String>)mvm).getFirst(FORM_PARAM_NAME))
				.log()
				.transform(Message.class, AdaptiveFormsService::renderBlankAdaptiveFormFromString)
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

	private GenericHandler<String> writeOutPayload() {
		return (GenericHandler<String>) (payload, headers) -> {
			FluentFormsSpringIntegrationApplication.log.atInfo().addArgument(payload).log("Found String '{}'.");
			return "Response '" + payload + "'.";
		};
	}

	private HttpRequestHandlerEndpointSpec httpPostMessageSource() {
		return Http.inboundGateway("/service/posttest")
				   .requestMapping(r->r.methods(HttpMethod.POST)
						   			   .consumes("application/xml")
						   )
				   ;
	}

	private HttpRequestHandlerEndpointSpec httpGetMessageSource() {
		return Http.inboundGateway("/service/gettest")
				   .requestMapping(r->r.methods(HttpMethod.GET)
						   			   .params(FORM_PARAM_NAME)
						   			   .produces("text/html")
						   )
				   ;
	}

	private MessageSource<String> stringMessageSource() {
		return (MessageSource<String>) () -> MessageBuilder.withPayload("testString").build();
	}

	private static void addHeaderForLaterRetrieval(HeaderEnricherSpec h) {
		h.header("enrichedHeader", "enrichedValue");
	}
	
	private static class IncomingData {
		private static final String VALUE1_XPATH = "//headerData/value1";
		
		public static String value1Xpath() { return VALUE1_XPATH; }
	}
	
	private static Map<String,XPathExpressionEvaluatingHeaderValueMessageProcessor> of(Map<String,String> in) {
		return in.entrySet().stream()
							.collect(
									Collectors.toMap(e->e.getKey(), e->new XPathExpressionEvaluatingHeaderValueMessageProcessor(e.getValue()))
									);
	}
}
