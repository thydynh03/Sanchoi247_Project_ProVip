package com.example.SanChoi247.model.service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final String fromEmail;

    @Autowired
    private JavaMailSender mailSender;

    public EmailService(@Value("${spring.mail.username}") String fromEmail) {
        this.fromEmail = fromEmail; // Khởi tạo giá trị
    }

    public boolean sendOtpEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your OTP");
            message.setText("OTP: " + otp);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CompletableFuture<Void> sendEmail(String to, String subject, String body) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);

                helper.setFrom(new InternetAddress(fromEmail, "SanChoi247 DO NOT REPLY"));
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body, true);

                mailSender.send(message);
            } catch (MessagingException | UnsupportedEncodingException e) {
                // Handle the exception here
                e.printStackTrace();
            }
        });
    }
}