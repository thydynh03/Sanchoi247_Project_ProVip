package com.example.SanChoi247.model.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String name;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private char gender; // Keep gender as char

    // Getter for gender
    public char getGender() {
        return gender;
    }

    // Setter for gender (if needed)
    public void setGender(char gender) {
        this.gender = gender;
    }
}
