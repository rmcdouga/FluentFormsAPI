package com._4point.aem.fluentforms.spring.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.fluentforms.api.Document;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(AdaptiveFormsServiceTest.TestConfiguration.class)
class AdaptiveFormsServiceTest {
	private static final String FORM_STR = "test/form1";

	@Autowired
	com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService ffAfService;
	
	@Autowired
	private AdaptiveFormsService underTest;

	@Mock private Document mockDoc;
	@Captor ArgumentCaptor<String> ffAfServiceForm;

	private enum TestScenario {
		renderBlankAdaptiveFormFromString(
				()->MessageBuilder.withPayload(FORM_STR).build(),
//				(Message<String> m)->AdaptiveFormsService.renderBlankAdaptiveFormFromString(m),
				m->AdaptiveFormsService.renderBlankAdaptiveFormFromString((Message<String>)m),
				m->{}
				)
		;
		final Supplier<Message<?>> input;
		final GenericTransformer<Message<?>, Message<?>> operationUnderTest;
		final Consumer<Message<?>> validation;
		
		private TestScenario(Supplier<Message<?>> input, GenericTransformer<Message<?>, Message<?>> operationUnderTest,
				Consumer<Message<?>> validation) {
			this.input = input;
			this.operationUnderTest = operationUnderTest;
			this.validation = validation;
		}

	}
	
	@ParameterizedTest
	@EnumSource
	void testRenderBlankAdaptiveForm(TestScenario testScenario) throws Exception {
		assertNotNull(underTest);
		assertNotNull(ffAfService);
		Mockito.when(ffAfService.renderAdaptiveForm(ffAfServiceForm.capture())).thenReturn(mockDoc);
		Message<?> result = testScenario.operationUnderTest.transform(testScenario.input.get());
		testScenario.validation.accept(result);
//		ffAfServiceForm.getValue();
	}

	@Configuration
	static class TestConfiguration {
		@Bean
		com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService ffAfService() {
			return Mockito.mock(com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.class);
		}

		@Bean
		AdaptiveFormsService afService(com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService afService) {
			return new AdaptiveFormsService(afService);
		}
	}
}
