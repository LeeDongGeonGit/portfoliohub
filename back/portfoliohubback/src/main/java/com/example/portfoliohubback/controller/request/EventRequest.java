package com.example.portfoliohubback.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

public class EventRequest {
    @Data
    public static class Create{
        private String title;
        private String content;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

    }
    @Data
    public static class Update{
        private String title;
        private String content;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

    }
}
