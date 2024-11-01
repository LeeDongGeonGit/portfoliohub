package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.ChatMessageEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {

    @Data
    @Builder
    public static class chatMessageOne {
        private Long messageId;
        private Long chatRoomId;
        private String senderId;
        private String receiverId;
        private String content;
        private LocalDateTime timestamp;
        private boolean messageRead; // 추가: 메시지 읽음 상태

        public static chatMessageOne of(ChatMessageEntity chatMessage) {
            return chatMessageOne.builder()
                    .messageId(chatMessage.getMessageId())
                    .chatRoomId(chatMessage.getChatRoom().getChatRoomId())
                    .senderId(chatMessage.getChatSender().getId())
                    .receiverId(chatMessage.getChatReceiver().getId())
                    .content(chatMessage.getMessageContent())
                    .timestamp(chatMessage.getMessageTimestamp())
                    .messageRead(chatMessage.isMessageRead())
                    .build();
        }
    }
}
