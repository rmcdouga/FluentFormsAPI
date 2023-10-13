package com._4point.aem.fluentforms.spring.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService.AdaptiveFormsServiceException;
import com._4point.aem.fluentforms.api.Document;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(AdaptiveFormsServiceTest.TestConfiguration.class)
class AdaptiveFormsServiceTest {

	@Autowired
	com._4point.aem.docservices.rest_services.client.af.AdaptiveFormsService ffAfService;
	
	@Autowired
	private AdaptiveFormsService underTest;

	@Mock
	private Document mockDoc;

	
	@Test
	void testRenderBlankAdaptiveForm() throws Exception {
		assertNotNull(underTest);
		assertNotNull(ffAfService);
		Mockito.when(ffAfService.renderAdaptiveForm(Mockito.anyString())).thenReturn(mockDoc);
		
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
