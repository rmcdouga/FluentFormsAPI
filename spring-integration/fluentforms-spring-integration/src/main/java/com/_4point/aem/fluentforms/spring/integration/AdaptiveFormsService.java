package com._4point.aem.fluentforms.spring.integration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;

public final class AdaptiveFormsService implements ApplicationContextAware {
	
	private final com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService afService;
	
	// The AEM default is to return the Adaptive Form in UTF-8 however we probably need to allow for other possibilities. 
	private final Charset afCharset = StandardCharsets.UTF_8; // TODO: Make this configurable with a configuration setting

	// Need the application context to retrieve beans for construction.
	private static ApplicationContext applicationContext;

	
	public AdaptiveFormsService(com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService afService) {
		this.afService = afService;
	}

	private static ApplicationContext applicationContext() {
		return Objects.requireNonNull(applicationContext, "applicationContext not initialized yet.");
	}

	public record RenderAfNoDataParameters(PathOrUrl template) {};
	public record RenderAfWithDataParameters(PathOrUrl template, Document data) {};

	// DSL should be able to create Transformers that take the following payloads:
	// void - Form is passes in header, no prepop data
	// byte[] - Form is passed in header, prepop dat in payload
	// String - Form is passed in header, prepop dat in payload
	// RenderAfNoDataParameters - form in payloar, no prepop data
	// RenderAfWithDataParameters - form and data is in payload
	// Maybe accept the xml payloads - form in headers, prepop data in payload
	
	public static class Parameters {
		private static final String PARAM_PREFIX = "com._4point.aem.fluentforms.spring.integration.AdaptiveFormsService.";
		private static final String FORM_KEY = PARAM_PREFIX + "form";
		
		public static String formKey() { return FORM_KEY; }
		public static PathOrUrl form(MessageHeaders mh) { return PathOrUrl.from(mh.get(FORM_KEY, String.class)); }
	}

	/**
	 * 
	 * 
	 * @param payload
	 * @return
	 */
	public static Message<String> renderBlankAdaptiveFormFromString(Message<String> payload) {
		return renderBlankAdaptiveForm(payload);
	}

	public static Message<String> renderBlankAdaptiveFormFromPath(Message<Path> payload) {
		return renderBlankAdaptiveForm(payload);
	}
	
	public static Message<String> renderBlankAdaptiveFormFromPathOrUrl(Message<PathOrUrl> payload) {
		return renderBlankAdaptiveForm(payload);
	}
	
	private static <T> Message<String> renderBlankAdaptiveForm(Message<T> payload) {
		AdaptiveFormsService afService = applicationContext().getBean(AdaptiveFormsService.class);
		String result = afService.renderAdaptiveForm(payload.getPayload().toString());
		return MessageBuilder.createMessage(result,payload.getHeaders());
	}
	
	/**
	 * Calls AEM to generate the Adaptive Form,
	 * 
	 *  Assumes that the HTML generated by AEM is in UTF-8
	 */
	private String renderAdaptiveForm(String form)  {
		try {
			Document doc = afService.renderAdaptiveForm(form);
			return new String(doc.getInputStream().readAllBytes(), afCharset);
		} catch (AdaptiveFormsServiceException | IOException e) {
			throw new AdaptiveFormServiceException(e);
		}
	}

	
	public static class AdaptiveFormServiceException extends RuntimeException {

		public AdaptiveFormServiceException() {
		}

		public AdaptiveFormServiceException(String message, Throwable cause) {
			super(message, cause);
		}

		public AdaptiveFormServiceException(String message) {
			super(message);
		}

		public AdaptiveFormServiceException(Throwable cause) {
			super(cause);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		AdaptiveFormsService.applicationContext = applicationContext;
	}
}
