package com.example.portfoliohubback.controller.request;

import lombok.Data;

public class ChatRoomRequest {

    //채팅걸기로 인해서 채팅방을 새로이 추가하기 위해 존재
    @Data
    public static class Create {
        private Long chatRoomId;
        private String chatSender;
        private String chatReceiver;
    }
}
