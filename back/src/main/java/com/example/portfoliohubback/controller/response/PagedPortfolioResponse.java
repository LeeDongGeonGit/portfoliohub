package com.example.portfoliohubback.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedPortfolioResponse {
    private List<PortfolioResponse> content;
    private boolean isFirst;
    private boolean isLast;
    private int totalPages;
    private long totalElements;
    private int currentPage;
}