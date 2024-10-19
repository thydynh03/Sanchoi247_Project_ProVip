package com.example.SanChoi247.model.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private char role;
}
