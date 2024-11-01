package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.ChatMessageEntity;
import com.example.portfoliohubback.entity.ChatRoomEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {

    //채팅방이 개설되었을 때 프론트에서 확인할 수 있또록 만들어진 정보를 넘김.
    @Data
    @Builder
    public static class chatRoomOne {
        private Long chatRoomId;
        private String senderId;
        private String receiverId;

        public static chatRoomOne of(ChatRoomEntity chatRoom) {

            return chatRoomOne.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .senderId(chatRoom.getChatSender().getId())
                    .receiverId(chatRoom.getChatReceiver().getId())
                    .build();
        }

    }

    // 채팅방리스트 중 1개의 정보를 담기위해 사용
    @Data
    @Builder
    public static class ChatRoomInfo {
        private Long chatRoomId;
        private String senderId;
        private String receiverId;
        private String lastMessage;
        private LocalDateTime lastMessageTimestamp;

        public static ChatRoomInfo of(ChatRoomEntity chatRoom, String lastMessage, LocalDateTime lastMessageTimestamp) {
            return ChatRoomInfo.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .senderId(chatRoom.getChatSender().getId())
                    .receiverId(chatRoom.getChatReceiver().getId())
                    .lastMessage(lastMessage)
                    .lastMessageTimestamp(lastMessageTimestamp)
                    .build();
        }
    }
    @Data
    @Builder
    public static class chatMessageOne {
        private Long messageId;
        private Long chatRoomId;
        private String senderId;
        private String receiverId;
        private String content;
        private String timestamp;

        // 생성자, getter, setter 메서드 생략

        public static chatMessageOne of(ChatMessageEntity messageEntity) {
            return chatMessageOne.builder()
                    .messageId(messageEntity.getMessageId())
                    .chatRoomId(messageEntity.getChatRoom().getChatRoomId())
                    .senderId(messageEntity.getChatSender().getId())
                    .receiverId(messageEntity.getChatReceiver().getId())
                    .content(messageEntity.getMessageContent())
                    .timestamp(messageEntity.getMessageTimestamp().toString())
                    .build();
        }
    }
}
