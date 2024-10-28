package com.example.SanChoi247.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt bảo vệ CSRF (mặc định)
            .authorizeHttpRequests(auth -> auth 
                .anyRequest().permitAll() // Cho phép tất cả các yêu cầu mà không cần xác thực
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login") // Trang đăng nhập tùy chỉnh
                .defaultSuccessUrl("/auth/login?googleLoginSuccess=true", true) // Chuyển hướng lại chính /auth/login với tham số xác thực
                .failureUrl("/login?error=true") // Điều hướng về trang lỗi nếu đăng nhập thất bại
            )
            .formLogin(form -> form.disable()) // Tắt form đăng nhập mặc định
            .httpBasic(basic -> basic.disable()); // Tắt HTTP Basic Authentication

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
