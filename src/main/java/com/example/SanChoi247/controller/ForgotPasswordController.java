package com.example.SanChoi247.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SanChoi247.service.EmailService;
import com.example.SanChoi247.service.OTPService;
import com.example.SanChoi247.service.UserService;

@Controller
public class ForgotPasswordController {
    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    private String userEmail; // Lưu email của người dùng để dùng ở trang xác minh OTP

    @GetMapping("forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) throws Exception {
        this.userEmail = email;

        if (userService.existsByEmail(email)) {
            String otp = otpService.generateOtp(email);
            boolean isSent = emailService.sendOtpEmail(email, otp);

            if (isSent) {
                model.addAttribute("message", "The OTP code has been sent to your email");
                return "redirect:/verify-otp"; // Chuyển hướng tới trang xác minh OTP
            } else {
                model.addAttribute("error", "There was an error sending the OTP code. Please try again later");
                return "auth/forgot-password"; // Trả lại trang forgot-password
            }
        } else {
            model.addAttribute("error", "Email does not exist in the system");
            return "auth/forgot-password"; // Trả lại trang forgot-password
        }
    }

    @GetMapping("verify-otp")
    public String showVerifyOtpForm() {
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam("otp") String otp, Model model) {
        if (otpService.verifyOtp(userEmail, otp)) {
            return "redirect:/reset-password"; // Nếu OTP đúng, chuyển đến trang đặt lại mật khẩu
        } else {
            model.addAttribute("error", "OTP code is incorrect. Please try again");
            return "auth/verify-otp"; // Trả lại trang xác minh OTP
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "auth/reset-password"; // Hiển thị form nhập mật khẩu mới
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Password do not match");
            return "auth/reset-password"; // Trả lại trang đặt lại mật khẩu
        }

        // Cập nhật mật khẩu mới vào database
        boolean isReset = userService.updatePasswordbyEmail(userEmail, newPassword);

        if (isReset) {
            model.addAttribute("message", "Change Successfully"); // Thông báo thành công
            return "auth/login"; // Chuyển về trang đăng nhập hoặc trang chính
        } else {
            model.addAttribute("error", "There was an error changing the password. Please try again");
            return "auth/reset-password"; // Trả lại trang đặt lại mật khẩu
        }
    }
}