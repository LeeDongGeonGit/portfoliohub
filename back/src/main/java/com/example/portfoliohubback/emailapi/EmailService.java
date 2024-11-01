package com.example.portfoliohubback.emailapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;



import lombok.AllArgsConstructor;

@Async
@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rolend0305@gmail.com");
        message.setTo(mailDto.getEmailAddr());
        message.setSubject("인증번호" + mailDto.getEmailTitle());
        message.setText("인증번호 " + mailDto.getEmailContent());
        emailSender.send(message);
    }
}
