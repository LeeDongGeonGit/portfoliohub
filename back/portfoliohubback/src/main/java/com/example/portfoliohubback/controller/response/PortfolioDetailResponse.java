package com.example.portfoliohubback.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDetailResponse {
    private Long id;
    private String createdAt;
    private String position;
    private String career;
    private Long count;
    private String profile;
    private String content;
    private String userId;
    private boolean isOwner;
}
