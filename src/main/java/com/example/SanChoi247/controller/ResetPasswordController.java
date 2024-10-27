package com.example.SanChoi247.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.service.UserService;

@Controller
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    private String userEmail; // Lưu trữ email người dùng để sử dụng khi đổi mật khẩu

    // Đổi đường dẫn thành "/update-password" hoặc một cái gì đó tương tự
    @PostMapping("/update-password") // Đổi đường dẫn
    public String processResetPassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Password do not match");
            return "auth/reset-password"; // Trả lại trang đặt lại mật khẩu
        }

        // Gọi phương thức updatePassword với cả email và mật khẩu mới
        boolean isUpdated = userService.updatePasswordbyEmail(userEmail, newPassword);

        if (isUpdated) {
            model.addAttribute("message", "Change password successfully");
            return "auth/login"; // Chuyển đến trang đăng nhập
        } else {
            model.addAttribute("error", "An error occurred while changing the password. Please try again");
        }

        return "auth/reset-password"; // Trả lại trang đặt lại mật khẩu
    }
}