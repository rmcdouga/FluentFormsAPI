package com._4point.aem.fluentforms.spring.integration;

public non-sealed interface PdfPayload extends PagedPayload {
	public static final String CONTENT_TYPE = "application/pdf";
	
	@Override
	default String contentType() {
		return CONTENT_TYPE;
	}
}
