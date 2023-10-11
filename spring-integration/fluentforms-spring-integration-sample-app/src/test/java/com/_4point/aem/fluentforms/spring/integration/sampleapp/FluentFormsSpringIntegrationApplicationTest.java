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

	private static final String EXPECTED_FORM_NAME = "foo";

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
	public void whenHttpPostRequestArrives_getResponse() {
		given()
			.baseUri("http://localhost:" + port)
			.body(TEST_XML)
			.contentType(ContentType.XML)
		.when()
			.post("/service/posttest")
		.then()
			.statusCode(200)
			.body(allOf(containsString("Response"), containsString("Some replaced text")));	
	}

	@Test
	public void whenHttpGetRequestArrives_getResponse() {
		given()
			.baseUri("http://localhost:" + port)
		.when()
			.param("form", EXPECTED_FORM_NAME)
			.get("/service/gettest")
		.then()
			.statusCode(200)
			.body(allOf(containsString("<html>"),containsString("</html>"),containsString(EXPECTED_FORM_NAME)));	
	}

}
