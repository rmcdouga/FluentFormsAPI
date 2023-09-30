package com._4point.aem.fluentforms.spring.integration.sampleapp;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringIntegrationApplication.class, 
				properties = {"fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
							  "fluentforms.aem.user=user", "fluentforms.aem.password=password"})
class FluentFormsSpringIntegrationApplicationTest {

	@LocalServerPort
	int port;
	
	
	@Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }

	@Test
	public void whenHttpRequestArrives_getResponse() {
		given()
			.baseUri("http://localhost:" + port)
			.body("Some Body Text")
		.when()
			.post("/service/test")
		.then()
			.statusCode(200)
			.body(containsString("Response"));	
	}
}
