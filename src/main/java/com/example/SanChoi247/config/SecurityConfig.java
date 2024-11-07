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
                .csrf((csrf) -> csrf.disable()) // Tắt bảo vệ CSRF
                .authorizeHttpRequests((authz) -> authz
                        .anyRequest().permitAll() // Cho phép tất cả các yêu cầu mà không cần xác thực
                )
                .formLogin((formLogin) -> formLogin.disable()) // Tắt trang đăng nhập mặc định của Spring Security
                .httpBasic((basic) -> basic.disable()); // Tắt cả cơ chế xác thực HTTP Basic

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}