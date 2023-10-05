package com._4point.aem.fluentforms.spring.integration.sampleapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

public class DeliveryService implements GenericTransformer<Message<String>, String> {
	private final static Logger log = LoggerFactory.getLogger(DeliveryService.class);

	@Override
	public String transform(Message<String> source) {
		log.atInfo().addArgument(source.getPayload()).log("Found payload '{}'.");
		log.atInfo().addArgument(Parameters.param1(source.getHeaders())).log("Found para value '{}'.");
		return source.getPayload();
	}
	
	public static class Parameters {
		private static final String PARAM_PREFIX = "com._4point.aem.fluentforms.spring.integration.sampleapp.DeliveryGateway.";
		private static final String PARAM1_KEY = PARAM_PREFIX + "param1";
		
		public static String param1Key() { return PARAM1_KEY; }
		public static String param1(MessageHeaders mh) { return mh.get(PARAM1_KEY, String.class); }
	}

}
