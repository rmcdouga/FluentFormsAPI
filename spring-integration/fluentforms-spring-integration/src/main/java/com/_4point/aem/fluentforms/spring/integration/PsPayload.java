package com._4point.aem.fluentforms.spring.integration;

public non-sealed interface PsPayload extends PagedPayload {
	public static final String CONTENT_TYPE = "application/postscript";
	
	@Override
	default String contentType() {
		return CONTENT_TYPE;
	}
}
