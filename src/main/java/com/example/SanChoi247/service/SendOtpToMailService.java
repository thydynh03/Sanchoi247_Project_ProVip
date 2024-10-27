package com.example.SanChoi247.service;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class SendOtpToMailService {
    private final JavaMailSender javaMailSender;

    private LocalDateTime lastSentTime; // Lưu thời gian gửi OTP cuối
    private static final int OTP_COOLDOWN_SECONDS = 5; // Thời gian chờ 60 giây

    @Autowired
    public SendOtpToMailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendOtpService(String email) {
        if (!canSendOtp()) {
            throw new RuntimeException("Please wait before sending another OTP.");
        }

        String otp = generateOtp();
        try {
            sendOtpToMail(email, otp);
            lastSentTime = LocalDateTime.now(); // Cập nhật thời gian gửi OTP
        } catch (Exception e) {
            throw new RuntimeException("Error while sending OTP to email", e);
        }
        return otp; // Trả về OTP
    }

    private boolean canSendOtp() {
        if (lastSentTime == null) {
            return true; // Nếu chưa gửi OTP lần nào
        }
        long secondsSinceLastSend = ChronoUnit.SECONDS.between(lastSentTime, LocalDateTime.now());
        return secondsSinceLastSend >= OTP_COOLDOWN_SECONDS; // Kiểm tra thời gian chờ
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpToMail(String email, String otp) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("OTP for email verification");
        mimeMessageHelper.setText("Hello,   " + //
                        "\n" + //
                        "may thang ngu\n" +
                        "Your OTP is: " + otp);
        javaMailSender.send(mimeMessage);
    }
}
