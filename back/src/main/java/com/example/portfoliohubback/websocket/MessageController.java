package com.example.portfoliohubback.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        logger.info("Received message: {}", chatMessage);

        // 메시지를 수신자에게 전송
        String destination = "/queue/messages/" + chatMessage.getChatReceiver();
        logger.info("Sending message to destination: {}", destination);
        messagingTemplate.convertAndSend(destination, chatMessage);
        logger.info("Message sent to destination: {}", destination);
    }
}