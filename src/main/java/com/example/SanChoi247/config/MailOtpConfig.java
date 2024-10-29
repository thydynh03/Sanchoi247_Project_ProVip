package com.example.SanChoi247.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailOtpConfig {
    @Value("${spring.mail.host}")  // Đúng cú pháp
    private String mailHost;
    
    @Value("${spring.mail.port}")  // Đúng cú pháp
    private String mailPort;
    
    @Value("${spring.mail.username}")  // Đúng cú pháp
    private String mailUsername;
    
    @Value("${spring.mail.password}")  // Đúng cú pháp
    private String mailPassword;

    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailHost);
        javaMailSender.setPort(Integer.parseInt(mailPort));  // chuyển port thành số nguyên
        javaMailSender.setUsername(mailUsername);
        javaMailSender.setPassword(mailPassword);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");  // dùng TLS
        return javaMailSender;
    }
}




