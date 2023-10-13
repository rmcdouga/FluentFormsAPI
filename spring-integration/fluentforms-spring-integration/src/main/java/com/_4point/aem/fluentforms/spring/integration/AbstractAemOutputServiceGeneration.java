//package com._4point.aem.fluentforms.spring.integration;
//
//import org.springframework.integration.core.GenericTransformer;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageHeaders;
//
//abstract class AbstractAemOutputServiceGeneration implements GenericTransformer<Message<Object>, PagedPayload>  {
//
//	@Override
//	public PagedPayload transform(Message<Object> source) {
//		// TODO Convert object to byte[] (or maybe Document, if we need to support inputStream objects
//		return null;
//	}
//
//	public abstract PagedPayload doTransform(byte[] payload, MessageHeaders);
//	
//}
