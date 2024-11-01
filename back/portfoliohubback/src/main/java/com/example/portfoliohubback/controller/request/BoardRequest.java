package com.example.portfoliohubback.controller.request;

import lombok.Data;

import java.time.LocalDateTime;

public class BoardRequest {
    @Data
    public static class Create{
        private String projectName;
        private LocalDateTime projectStartDate;
        private LocalDateTime projectEndDate;
        private String projectLocal;
        private int projectMemberCount;
        private String projectDevelopmentField;
        private String projectDescription;
    }
    @Data
    public static class Update{
        private String projectName;
        private LocalDateTime projectStartDate;
        private LocalDateTime projectEndDate;
        private String projectLocal;
        private int projectMemberCount;
        private String projectDevelopmentField;
        private String projectDescription;
    }
}
