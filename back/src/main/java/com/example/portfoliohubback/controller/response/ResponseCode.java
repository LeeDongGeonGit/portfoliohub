package com.example.portfoliohubback.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200000),
    Created(201000),
    BAD_REQUEST(400000),
    NOT_FOUND(404000),
    SUCCESS(201000),
    UNAUTHORIZED(401000),
    FORBIDDEN(403000);

    int code;
}
