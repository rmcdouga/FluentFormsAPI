package com._4point.aem.fluentforms.spring.integration;

import java.util.OptionalInt;

/**
 * PagedPayload is a Payload whose data has pages 
 * (and more specifically has a number of pages).
 *
 */
public sealed interface PagedPayload permits PclPayload, PdfPayload, PsPayload, AbstractInMemoryPayloadImpl {
	byte[] bytes();
	OptionalInt numPages();
	String contentType();
}
