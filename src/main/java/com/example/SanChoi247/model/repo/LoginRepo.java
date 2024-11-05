package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Nhập PasswordEncoder
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.User;

@Repository
public class LoginRepo {

    @Autowired
    private PasswordEncoder passwordEncoder; // Tiêm PasswordEncoder

    public User checkLogin(String username, String password) throws Exception {
        Class.forName(Baseconnection.nameClass);
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return null; // Người dùng không tồn tại
            }

            // Lấy mật khẩu đã mã hóa từ cơ sở dữ liệu
            String encodedPassword = rs.getString("password");

            // Kiểm tra xem mật khẩu nhập vào có khớp với mật khẩu đã mã hóa hay không
            if (!passwordEncoder.matches(password, encodedPassword)) {
                return null; // Mật khẩu không hợp lệ
            }

            // Nếu mật khẩu khớp, tạo đối tượng User
            int uid = rs.getInt("uid");
            String name = rs.getString("name");
            Date dob = rs.getDate("dob");
            char gender = rs.getString("gender").charAt(0);
            String phone = rs.getString("phone");
            String email = rs.getString("email");
            String username1 = rs.getString("username");
            String avatar = rs.getString("avatar");
            String ten_san = rs.getString("ten_san");
            String address = rs.getString("address");
            String img_san1 = rs.getString("img_san1");
            String img_san2 = rs.getString("img_san2");
            String img_san3 = rs.getString("img_san3");
            String img_san4 = rs.getString("img_san4");
            String img_san5 = rs.getString("img_san5");
            int status = rs.getInt("status");
            char role = rs.getString("role").charAt(0);
            User user = new User(uid, name, dob, gender, phone, email, username1, encodedPassword, avatar, ten_san,
                    address,
                    img_san1, img_san2, img_san3, img_san4, img_san5, status, role);
            return user;
        }
    }

    public boolean checkOldPassword(int userId, String oldPassword) throws Exception {
        String query = "SELECT password FROM users WHERE uid = ?";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Kiểm tra mật khẩu cũ với mật khẩu đã mã hóa
                return passwordEncoder.matches(oldPassword, storedPassword);
            }
        }
        return false; // Không tìm thấy người dùng hoặc mật khẩu không đúng
    }
}
