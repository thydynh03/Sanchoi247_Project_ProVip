package com.example.SanChoi247.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class OTPService {

    private final Map<String, String> otpStore = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    // Tạo mã OTP và lưu vào HashMap theo email
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(email, otp);
        return otp;
    }

    // Kiểm tra OTP có khớp với email không
    public boolean verifyOtp(String email, String otp) {
        return otpStore.containsKey(email) && otpStore.get(email).equals(otp);
    }

    // Cập nhật mật khẩu người dùng (cần có logic thực tế để cập nhật vào database)
    public boolean resetPassword(String email, String newPassword) {
        // TODO: Thêm logic để cập nhật mật khẩu vào database (ví dụ, sử dụng UserRepository)
        // Giả sử bạn có UserRepository để cập nhật thông tin người dùng
        // User user = userRepository.findByEmail(email);
        // user.setPassword(newPassword);  // Cần mã hóa mật khẩu trước khi lưu vào DB
        // userRepository.save(user);
        return true; // Trả về true nếu cập nhật mật khẩu thành công
    }
}