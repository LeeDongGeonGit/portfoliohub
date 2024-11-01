package com.example.portfoliohubback.emailapi;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class EmailNumEntity {
    private LocalDateTime endTime;
    private String email;
    private String num;
    public EmailNumEntity(String email, String num){
        endTime = LocalDateTime.now().plusMinutes(5);
        this.email =email;
        this.num = num;
    }
}

