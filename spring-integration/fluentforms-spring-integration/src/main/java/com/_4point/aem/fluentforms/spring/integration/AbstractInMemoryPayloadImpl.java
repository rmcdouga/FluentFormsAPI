package com._4point.aem.fluentforms.spring.integration;

import java.util.OptionalInt;

public abstract non-sealed class AbstractInMemoryPayloadImpl implements PagedPayload {
	
	private final byte[] bytes;
	private final OptionalInt numPages;
	
	public AbstractInMemoryPayloadImpl(byte[] bytes) {
		this.bytes = bytes;
		this.numPages = OptionalInt.empty();
	}

	public AbstractInMemoryPayloadImpl(byte[] bytes, int numPages) {
		this.bytes = bytes;
		this.numPages = OptionalInt.of(numPages);
	}

	@Override
	public byte[] bytes() {
		return bytes;
	}
	
	@Override
	public OptionalInt numPages() {
		return numPages;
	}
}
