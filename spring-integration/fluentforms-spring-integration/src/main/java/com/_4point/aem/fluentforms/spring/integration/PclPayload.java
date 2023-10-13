package com._4point.aem.fluentforms.spring.integration;

public non-sealed interface PclPayload extends PagedPayload {
	public static final String CONTENT_TYPE = "application/vnd.hp-pcl";

	@Override
	default String contentType() {
		return CONTENT_TYPE;
	}
}
