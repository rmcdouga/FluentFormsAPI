package com._4point.aem.fluentforms.spring.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import com._4point.aem.fluentforms.spring.AemConfiguration;
import com._4point.aem.fluentforms.spring.FluentFormsAutoConfiguration;

@SpringBootTest(classes = {	com._4point.aem.fluentforms.spring.integration.FluentFormsSpringIntegrationAutoConfigurationTest.TestApplication.class,
							FluentFormsAutoConfiguration.class,
							FluentFormsSpringIntegrationAutoConfiguration.class,
						  },
				properties = {
						"fluentforms.aem.servername=localhost", 
						"fluentforms.aem.port=4502", 
						"fluentforms.aem.user=admin",		 
						"fluentforms.aem.password=admin)",
						}
				)
class FluentFormsSpringIntegrationAutoConfigurationTest {

	@Test
	void testAdaptiveFormsService(@Autowired AdaptiveFormsService service) {
		assertNotNull(service);
	}

	@SpringBootApplication
	@EnableConfigurationProperties({AemConfiguration.class})
	public static class TestApplication {
		public static void main(String[] args) {
			SpringApplication.run(TestApplication.class, args);
		}
	}
}
