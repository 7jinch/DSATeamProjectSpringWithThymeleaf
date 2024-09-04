package com.example.tabitabi.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            //이거는 메일 제목
            String subject = "이메일 인증코드"; 
            //이거는 본문 html처럼 꾸미면 예쁘게 보내짐, 하지만 난 그럴 능력과 체력이 없어요
            String body = "<p>이메일 인증 코드: " + verificationCode + "</p>";

            //이 to는 이메일주소, 그래서 값을 받아오는거
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }
}