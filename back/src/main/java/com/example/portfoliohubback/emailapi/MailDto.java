package com.example.portfoliohubback.emailapi;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailDto {
    private String emailAddr;	// 받는 사람
    private String emailTitle; // 메일 제목
    private String emailContent;// 메일 내용
}
