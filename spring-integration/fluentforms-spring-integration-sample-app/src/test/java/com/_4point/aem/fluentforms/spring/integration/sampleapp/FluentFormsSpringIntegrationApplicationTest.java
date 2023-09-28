package com._4point.aem.fluentforms.spring.integration.sampleapp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, 
				classes = FluentFormsSpringIntegrationApplication.class, 
				properties = {"fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
							  "fluentforms.aem.user=user", "fluentforms.aem.password=password"})
class FluentFormsSpringIntegrationApplicationTest {

	@Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }

}
