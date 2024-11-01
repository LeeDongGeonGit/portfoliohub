package com.example.portfoliohubback.controller.request;

import lombok.Data;

public class PortfolioRequest {
    @Data
    public static class Create{
        private String position;
        private String career;
        private String content;
        private String profile;
    }
    @Data
    public static class Update {
        private String career;
        private String position;
        private String content;
        private String profile;
    }
}
