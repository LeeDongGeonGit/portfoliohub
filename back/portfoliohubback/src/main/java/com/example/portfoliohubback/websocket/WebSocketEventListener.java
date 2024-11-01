package com.example.portfoliohubback.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("웹소켓이 연결되었습니다");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        System.out.println("웹소켓이 연결이 해제되었습니다");
    }

    @MessageMapping("/chat")
    public void handleChatMessage(@Payload ChatMessage chatMessage) {
        messagingTemplate.convertAndSend(
                "/queue/messages/" + chatMessage.getChatReceiver(),
                chatMessage
        );
    }
}
