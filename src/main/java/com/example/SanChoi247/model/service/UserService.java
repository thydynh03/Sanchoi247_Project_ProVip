package com.example.SanChoi247.model.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.repo.Baseconnection;
import com.example.SanChoi247.model.repo.UserRepo;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsByEmail(String email) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("SELECT 1 FROM users WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();
        rs.close();
        ps.close();
        con.close();
        return exists;
    }

    public boolean updatePasswordbyEmail(String email, String newPassword) {
        try {
            // Mã hóa mật khẩu mới
            String encodedPassword = passwordEncoder.encode(newPassword);
            // Cập nhật vào cơ sở dữ liệu
            int rowsUpdated = userRepo.updatePasswordByEmail(email, encodedPassword);
            return rowsUpdated > 0; // Trả về true nếu cập nhật thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Trả về false nếu có lỗi
        }
    }

    public boolean login(String email, String rawPassword) {
        // Lấy mật khẩu mã hóa từ cơ sở dữ liệu
        String encodedPassword = userRepo.getPasswordByEmail(email);

        // So sánh mật khẩu nhập vào với mật khẩu đã mã hóa
        if (encodedPassword != null) {
            return passwordEncoder.matches(rawPassword, encodedPassword); // Trả về true nếu khớp
        }
        return false; // Trả về false nếu không tìm thấy email
    }   
    public boolean requestFieldOwner(int uid) {
        try {
            userRepo.requestFieldOwner(uid);
            return true; // Request submitted successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Request failed
        }
    }

    public List<User> getPendingRequests() {
        try {
            // You can implement a similar method in `UserRepo` to fetch users with status = 1
            return userRepo.getUsersByStatus(1); // Get users with pending requests (status = 1)
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list if an error occurs
        }
    }

    public boolean approveFieldOwner(int uid, boolean isApproved) {
        try {
            userRepo.approveFieldOwnerRequest(uid, isApproved);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int uid) {
        try {
            return userRepo.getUserById(uid);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if user is not found
        }
    }

}
