package com.example.portfoliohubback.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

public class ChatMessageRequest {

    //사용자가 채팅을 칠 떄 추가될 인스탄스
    @Data
    public static class Create {
        private String chatReceiver; // 수신자 아이디
        private String messageContent; // 메시지 내용
        private LocalDateTime messageTimestamp; // 메시지 생성 시간
    }
}

