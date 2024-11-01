package com.example.portfoliohubback.controller.response;

import com.example.portfoliohubback.entity.EventEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventResponse {
    @Data
    @Builder
    public static class eventOne {
        private Long id;
        private String userId;
        private String title;
        private String content;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static eventOne of(EventEntity event) {

            return eventOne.builder()
                    .id(event.getId())
                    .userId((event.getUser().getId()))
                    .title(event.getTitle())
                    .content(event.getContent())
                    .startTime(event.getStartTime())
                    .endTime(event.getEndTime())
                    .createdAt(event.getCreatedAt())
                    .updatedAt(event.getUpdatedAt())
                    .build();
        }
    }

}
