package com.slerpio.teachme.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slerp.core.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatTest {
    private CompletableFuture<Domain> future;
    private StompSession session;
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Before
    public void prepare() throws ExecutionException, InterruptedException {
        String url = "ws://localhost:3004/messaging";
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new MappingJackson2MessageConverter());
        session = client.connect(url, new StompSessionHandlerAdapter() {}).get();
        future  = new CompletableFuture<>();
    }

    @Test
    public void testListenChat() throws ExecutionException, InterruptedException {
        session.subscribe("/app/chats/3/4/0/10", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Domain.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Domain response = (Domain) payload;
                future.complete(response);
            }
        });
        Domain result = future.get();
        log.info("result : {}", result);
        session.disconnect();
    }

}
