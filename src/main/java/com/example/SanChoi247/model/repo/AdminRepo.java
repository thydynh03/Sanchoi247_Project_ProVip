package com.example.SanChoi247.model.repo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.dto.AdminStatistics;
import com.example.SanChoi247.model.dto.TableAdminStatistics;
import com.example.SanChoi247.model.entity.User;

@Repository
public class AdminRepo {
    @Autowired
    UserRepo userRepo;

    public AdminStatistics getStatisticsForAdmin() throws Exception {
        Class.forName(Baseconnection.nameClass);
        AdminStatistics stats = new AdminStatistics();

        String sql = "SELECT "
                + "(SELECT SUM(price) FROM booking WHERE status = 0) AS total_sold_amount_active_stadiums, "
                + "(SELECT COUNT(*) FROM users s WHERE s.status = 4) AS total_ongoing_stadiums, "
                + "(SELECT COUNT(*) FROM san s WHERE s.is_approve = 0) AS total_approved_stadiums, "
                + "(SELECT COUNT(*) FROM san s WHERE s.is_approve = 3) AS total_rejected_stadiums, "
                + "(SELECT COUNT(*) FROM refundBooking rb WHERE rb.status = 3) AS total_refund_booking, "
                + "(SELECT COUNT(*) FROM san s WHERE s.is_approve = 1 AND s.eyeview > 0) AS total_passed_stadiums, "
                + "(SELECT COUNT(*) FROM users u WHERE u.role = 'C' AND u.status = 2) AS total_inactive_owners, "
                + "(SELECT COUNT(*) FROM users u WHERE u.role = 'C' AND u.status = 4) AS total_active_owners, "
                + "(SELECT COUNT(*) FROM users u WHERE u.role NOT IN ('a', 'b', 'p')) AS total_users_excluding_owners, "
                + "(SELECT COUNT(*) FROM users u WHERE u.role = 'b' OR u.role = 'p') AS total_banned_users";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                stats.setTotalSoldAmountActiveStadiums(rs.getDouble("total_sold_amount_active_stadiums"));
                stats.setTotalOngoingStadiums(rs.getInt("total_ongoing_stadiums"));
                stats.setTotalApprovedStadiums(rs.getInt("total_approved_stadiums"));
                stats.setTotalRejectedStadiums(rs.getInt("total_rejected_stadiums"));
                stats.setTotalRefundBooking(rs.getInt("total_refund_booking"));
                stats.setTotalPassStadiums(rs.getInt("total_passed_stadiums"));
                stats.setTotalInactiveOwners(rs.getInt("total_inactive_owners"));
                stats.setTotalActiveOwners(rs.getInt("total_active_owners"));
                stats.setTotalUsersExcludingOwners(rs.getInt("total_users_excluding_owners"));
                stats.setTotalBannedUsers(rs.getInt("total_banned_users"));
            }
        }

        return stats;
    }

    public List<Double> getDailyRevenue() throws Exception {
        Class.forName(Baseconnection.nameClass);
        List<Double> dailyRevenue = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);) {
            // Câu truy vấn SQL sử dụng bảng `booking` và `Schedulebooking`
            String sql = "SELECT DATE(b.date) AS `day`, SUM(b.price) AS daily_revenue " +
                    "FROM booking b " +
                    "JOIN Schedulebooking sb ON b.Sbooking_id = sb.Sbooking_id " +
                    "WHERE b.status = 0 AND b.date >= DATE_SUB(CURDATE(), INTERVAL 2 WEEK) " +
                    "GROUP BY DATE(b.date) " +
                    "ORDER BY DATE(b.date);";

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Lấy doanh thu theo ngày và thêm vào danh sách
                    dailyRevenue.add(rs.getDouble("daily_revenue"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving daily revenue", e);
        }
        return dailyRevenue;
    }

    public List<Double> getMonthlyRevenue() throws Exception {
        // Khởi tạo danh sách với 12 phần tử bằng 0.0
        List<Double> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, 0.0));

        // Lấy năm hiện tại
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password)) {
            // Truy vấn SQL sử dụng bảng `booking` và lấy doanh thu theo tháng
            String sql = "SELECT DATE_FORMAT(b.date, '%Y-%m') AS `month`, SUM(b.price) AS monthly_revenue "
                    + "FROM booking b "
                    + "WHERE b.status = 0 AND YEAR(b.date) = ? "
                    + "GROUP BY DATE_FORMAT(b.date, '%Y-%m') "
                    + "ORDER BY DATE_FORMAT(b.date, '%Y-%m');";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, currentYear); // Thiết lập năm hiện tại

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String month = rs.getString("month");
                        double revenue = rs.getDouble("monthly_revenue");

                        // Lấy tháng từ chuỗi định dạng 'yyyy-MM' và chuyển đổi thành số nguyên
                        int monthIndex = Integer.parseInt(month.split("-")[1]) - 1;

                        // Đặt doanh thu vào đúng vị trí trong danh sách
                        monthlyRevenue.set(monthIndex, revenue);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving monthly revenue", e);
        }
        return monthlyRevenue;
    }

    public List<TableAdminStatistics> getFieldRevenues() throws Exception {
        List<TableAdminStatistics> fieldRevenues = new ArrayList<>();
        String sql = "SELECT u.ten_san AS fieldName, u.name AS ownerName, "
                + "SUM(CASE WHEN b.status = 0 THEN b.price ELSE 0 END) AS revenue "
                + "FROM san s "
                + "JOIN users u ON s.uid = u.uid "
                + "JOIN booking b ON s.san_id = b.san_id "
                + "WHERE b.status = 0 "
                + "GROUP BY u.ten_san, u.name";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fieldName = rs.getString("fieldName");
                String ownerName = rs.getString("ownerName");
                double revenue = rs.getDouble("revenue");
                fieldRevenues.add(new TableAdminStatistics(fieldName, ownerName, revenue));
            }
        }

        return fieldRevenues;
    }

    public ArrayList<User> getAllUser() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        String query = "SELECT * FROM users WHERE role != 'A' AND  role != 'b' AND  role != 'p'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setUid(rs.getInt("uid"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setPhone(rs.getString("phone"));
            user.setDob(rs.getDate("dob"));
            user.setGender(rs.getString("gender").charAt(0));
            user.setAvatar(rs.getString("avatar"));
            user.setRole(rs.getString("role").charAt(0));
            user.setEmail(rs.getString("email"));
            user.setTen_san(rs.getString("ten_san"));
            user.setAddress(rs.getString("address"));
            user.setImg_san1(rs.getString("img_san1"));
            user.setImg_san2(rs.getString("img_san2"));
            user.setImg_san3(rs.getString("img_san3"));
            user.setImg_san4(rs.getString("img_san4"));
            user.setImg_san5(rs.getString("img_san5"));
            user.setStatus(rs.getInt("status"));
            users.add(user);
        }
        rs.close();
        ps.close();
        con.close();

        return users;
    }

    public void lockUser(int userId, char currentRole) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        String query;
        if (currentRole == 'U') {
            query = "UPDATE users SET role = 'b' WHERE uid = ?";
        } else if (currentRole == 'C') {
            query = "UPDATE users SET role = 'p', status = 0 WHERE uid = ?";
        } else {
            throw new IllegalArgumentException("Invalid role: " + currentRole);
        }

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, userId);
        int rowsUpdated = ps.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("User with ID " + userId + " has been locked.");
        } else {
            System.out.println("No user with ID " + userId + " found.");
        }

        ps.close();
        con.close();
    }

    public void unlockUser(int userId, char currentRole) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        String query;
        if (currentRole == 'b') {
            query = "UPDATE users SET role = 'U' WHERE uid = ?";
        } else if (currentRole == 'p') {
            query = "UPDATE users SET role = 'C', status = 4 WHERE uid = ?";
        } else {
            throw new IllegalArgumentException("Invalid role: " + currentRole);
        }

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, userId);
        int rowsUpdated = ps.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.println("User with ID " + userId + " has been unlocked.");
        } else {
            System.out.println("No user with ID " + userId + " found.");
        }

        ps.close();
        con.close();
    }

    public ArrayList<User> getAllBanner() throws Exception {
        ArrayList<User> users = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        String query = "SELECT * FROM users WHERE role = 'b' OR role = 'p'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setUid(rs.getInt("uid"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setPhone(rs.getString("phone"));
            user.setDob(rs.getDate("dob"));
            user.setGender(rs.getString("gender").charAt(0));
            user.setAvatar(rs.getString("avatar"));
            user.setRole(rs.getString("role").charAt(0));
            user.setEmail(rs.getString("email"));
            user.setTen_san(rs.getString("ten_san"));
            user.setAddress(rs.getString("address"));
            user.setImg_san1(rs.getString("img_san1"));
            user.setImg_san2(rs.getString("img_san2"));
            user.setImg_san3(rs.getString("img_san3"));
            user.setImg_san4(rs.getString("img_san4"));
            user.setImg_san5(rs.getString("img_san5"));
            user.setStatus(rs.getInt("status"));
            users.add(user);
        }
        rs.close();
        ps.close();
        con.close();

        return users;
    }

    public ArrayList<User> getAllActiveOwner() throws Exception {
        ArrayList<User> owner = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        String query = "SELECT * FROM users WHERE role = 'C'";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user = new User();
            user.setUid(rs.getInt("uid"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setPhone(rs.getString("phone"));
            user.setDob(rs.getDate("dob"));
            user.setGender(rs.getString("gender").charAt(0));
            user.setAvatar(rs.getString("avatar"));
            user.setRole(rs.getString("role").charAt(0));
            user.setEmail(rs.getString("email"));
            user.setTen_san(rs.getString("ten_san"));
            user.setAddress(rs.getString("address"));
            user.setImg_san1(rs.getString("img_san1"));
            user.setImg_san2(rs.getString("img_san2"));
            user.setImg_san3(rs.getString("img_san3"));
            user.setImg_san4(rs.getString("img_san4"));
            user.setImg_san5(rs.getString("img_san5"));
            user.setStatus(rs.getInt("status"));
            owner.add(user);
        }

        rs.close();
        ps.close();
        con.close();

        return owner;
    }
}
