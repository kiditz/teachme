package com.slerpio.teachme.api.controller;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class NotificationTest {
	private String url;
	private Logger log = LoggerFactory.getLogger(getClass());
	private CompletableFuture<Domain> future;
	//@Value("${local.server.port}")
	private int port = 3004;
	@Before
	public void setup() {
		
	}

	@Test
	public void testConnection() throws InterruptedException, ExecutionException, TimeoutException {
		url = "ws://localhost:" + port + "/messaging";
		future = new CompletableFuture<>();
		log.info("Url >> {}", url);
		
		WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
		client.setMessageConverter(new MappingJackson2MessageConverter());
		StompSession session = client.connect(url, new StompSessionHandlerAdapter() {
		}).get();
		session.subscribe("/topic/notification/087788044374", new StompFrameHandlerImpl(session));
		Domain result = future.get();
		log.info("result : {}", result);

	}
	private class StompFrameHandlerImpl implements StompFrameHandler {
	    StompSession session;
        public StompFrameHandlerImpl(StompSession session) {
            this.session = session;
        }
        @Override
		public Type getPayloadType(StompHeaders arg0) {
			return Domain.class;
		}

		@Override
		public void handleFrame(StompHeaders arg0, Object result) {
			Domain response = (Domain) result;
			session.send("/app/receiver", response);
            future.complete(response);
		}

	}
	
	public static void main(String[] args) throws Exception {
		new NotificationTest().testConnection();
	}
}
