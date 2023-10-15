package com._4point.aem.fluentforms.spring.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

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

import com._4point.aem.fluentforms.api.Document;
import com._4point.aem.fluentforms.api.PathOrUrl;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(AdaptiveFormsServiceTest.TestConfiguration.class)
class AdaptiveFormsServiceTest {
	private static final String FORM_STR = "test/form1";
	private static final String MOCK_HTML = "<html></html>";

	@Autowired
	com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService ffAfService;
	
	@Autowired
	private AdaptiveFormsService underTest;

	@Mock private Document mockDoc;
	@Captor ArgumentCaptor<String> ffAfServiceForm;

	@SuppressWarnings("unchecked")
	private enum TestScenario {
		renderBlankAdaptiveFormFromString(
				()->MessageBuilder.withPayload(FORM_STR).build(),
				m->AdaptiveFormsService.renderBlankAdaptiveFormFromString((Message<String>)m)
				),
		renderBlankAdaptiveFormFromPath(
				()->MessageBuilder.withPayload(Path.of(FORM_STR)).build(),
				m->AdaptiveFormsService.renderBlankAdaptiveFormFromPath((Message<Path>)m)
				),
		renderBlankAdaptiveFormFromPathOrUrl(
				()->MessageBuilder.withPayload(PathOrUrl.from(FORM_STR)).build(),
				m->AdaptiveFormsService.renderBlankAdaptiveFormFromPathOrUrl((Message<PathOrUrl>)m)
				),
		;
		final Supplier<Message<?>> input;
		final GenericTransformer<Message<?>, Message<?>> operationUnderTest;
		
		private TestScenario(Supplier<Message<?>> input, GenericTransformer<Message<?>, Message<?>> operationUnderTest) {
			this.input = input;
			this.operationUnderTest = operationUnderTest;
		}

	}
	
	@ParameterizedTest
	@EnumSource
	void testRenderBlankAdaptiveForm(TestScenario testScenario) throws Exception {
		// Given
		assertNotNull(underTest);
		assertNotNull(ffAfService);
		Mockito.when(ffAfService.renderAdaptiveForm(ffAfServiceForm.capture())).thenReturn(mockDoc);
		Mockito.when(mockDoc.getInputStream()).thenReturn(new ByteArrayInputStream(MOCK_HTML.getBytes()));
		
		// When
		Message<?> result = testScenario.operationUnderTest.transform(testScenario.input.get());
		
		// Then
		// Validate that the function returned rhe mock HTML
		assertEquals(MOCK_HTML, result.getPayload());
		// Validate that the value passed to the mock Fluent Forms AdaptiveFormsService was correct.
		// Wrap in Path object to account for file system separator differences. 
		assertEquals(Path.of(FORM_STR), Path.of(ffAfServiceForm.getValue()));
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
