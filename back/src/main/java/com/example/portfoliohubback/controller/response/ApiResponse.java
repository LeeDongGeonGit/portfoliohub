package com.example.portfoliohubback.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<E> {
    private ResponseCode code;
    private E data;
    private boolean result;

    public static <E> ApiResponse<E> response(boolean result,ResponseCode code, E data){
        return ApiResponse.<E>builder()
                .result(result)
                .code(code)
                .data(data)
                .build();
    }
}
