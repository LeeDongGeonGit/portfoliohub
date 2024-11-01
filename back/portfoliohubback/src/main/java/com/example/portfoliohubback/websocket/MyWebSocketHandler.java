package com.example.portfoliohubback.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지 수신
        String payload = message.getPayload();

        // 받은 메시지를 처리하는 로직을 여기에 추가할 수 있습니다.
        // 여기서는 받은 메시지를 그대로 다시 클라이언트(수신자)로 전송하는 예시를 보여줍니다.
        session.sendMessage(new TextMessage("서버에서 받은 메시지: " + payload));
    }
}
