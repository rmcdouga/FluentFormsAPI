package com._4point.aem.fluentforms.spring.integration.sampleapp;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, 
				classes = FluentFormsSpringIntegrationApplication.class, 
				properties = {"fluentforms.aem.servername=localhost", "fluentforms.aem.port=4502",
							  "fluentforms.aem.user=user", "fluentforms.aem.password=password"})
class FluentFormsSpringIntegrationApplicationTest {

	@LocalServerPort
	int port;
	
	private static final String TEST_XML = 
			"""
			<root>
				<payload>Mixed Case String</payload>
			</root>
			""";
	
	@Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }

	@Test
	public void whenHttpRequestArrives_getResponse() {
		given()
			.baseUri("http://localhost:" + port)
			.body(TEST_XML)
			.contentType(ContentType.XML)
		.when()
			.post("/service/test")
		.then()
			.statusCode(200)
			.body(allOf(containsString("Response"), containsString("Some replaced text")));	
	}
}
