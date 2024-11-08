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
import com.example.SanChoi247.model.dto.TableOwnerStatistics;
import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.Booking.PaymentStatus;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;

@Repository
public class AdminRepo {
    @Autowired
    UserRepo userRepo;
    @Autowired
    SanRepo sanRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;

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
                + "(SELECT COUNT(*) FROM users u WHERE u.role = 'b' OR u.role = 'p') AS total_banned_users, "
                + "(SELECT COUNT(*) FROM booking b WHERE b.status = 7) AS total_request_refund";

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
                stats.setTotalRequestRefund(rs.getInt("total_request_refund"));
            }
        }

        return stats;
    }

    public List<TableAdminStatistics> getFieldRevenues() throws Exception {
        Class.forName(Baseconnection.nameClass);
        List<TableAdminStatistics> fieldRevenues = new ArrayList<>(); // Corrected the type here

        String sql = "SELECT u.ten_san AS stadium_name, u.username AS owner_name, SUM(b.price) AS revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "JOIN users u ON s.uid = u.uid " +
                "WHERE b.status = 0 " + // Added a space after 'status = 0'
                "GROUP BY u.ten_san, u.username " +
                "ORDER BY revenue DESC";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TableAdminStatistics stats = new TableAdminStatistics();
                    stats.setStadiumName(rs.getString("stadium_name"));
                    stats.setOwnerName(rs.getString("owner_name"));
                    stats.setRevenue(rs.getDouble("revenue"));

                    fieldRevenues.add(stats);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error retrieving field revenues", e);
        }

        return fieldRevenues;
    }

    public List<Double> getMonthlyRevenueForAdmin() throws Exception {
        List<Double> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, 0.0)); // Initialize list with 12 entries,
                                                                                     // all set to 0.0
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String sql = "SELECT MONTH(b.date) AS month, SUM(b.price) AS monthly_revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE b.status = 0 AND YEAR(b.date) = ? " +
                "GROUP BY MONTH(b.date) " +
                "ORDER BY MONTH(b.date);";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, currentYear); // Set the current year

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month") - 1; // SQL months start at 1, adjust to 0-based index
                    double revenue = rs.getDouble("monthly_revenue");

                    // Set the revenue in the corresponding position in the list
                    monthlyRevenue.set(month, revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving monthly revenue for all owners", e);
        }
        return monthlyRevenue;
    }

    public List<Double> getDailyRevenueForAdmin() throws Exception {
        List<Double> dailyRevenue = new ArrayList<>(Collections.nCopies(31, 0.0)); // Initialize list with 31 entries,
                                                                                   // all set to 0.0
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Adjust for 0-based month in Calendar

        String sql = "SELECT DAY(b.date) AS day, SUM(b.price) AS daily_revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE b.status = 0 AND YEAR(b.date) = ? AND MONTH(b.date) = ? " +
                "GROUP BY DAY(b.date) " +
                "ORDER BY DAY(b.date);";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, currentYear); // Set the current year
            ps.setInt(2, currentMonth); // Set the current month

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day") - 1; // SQL days start at 1, adjust to 0-based index
                    double revenue = rs.getDouble("daily_revenue");

                    // Set the revenue in the corresponding position in the list
                    dailyRevenue.set(day, revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving daily revenue for all owners", e);
        }

        return dailyRevenue;
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

    public ArrayList<Booking> getAllUserBookingForAdmin() throws Exception {
        ArrayList<Booking> bookings = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        String query = "SELECT * FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE (b.status = 0)";
        PreparedStatement ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Booking booking = new Booking();
            booking.setBooking_id(rs.getInt("booking_id"));
            int sid = rs.getInt("san_id");
            San san = sanRepo.getSanById(sid);
            booking.setSan(san);
            int uid1 = rs.getInt("uid");
            User user = userRepo.getUserById(uid1);
            booking.setUser(user);
            booking.setDate(rs.getTimestamp("date").toLocalDateTime());
            int Sbooking_id = rs.getInt("slot");
            ScheduleBooking scheduleBooking = scheduleBookingRepo.getScheduleBookingById(Sbooking_id);
            booking.setScheduleBooking(scheduleBooking);
            booking.setTotalprice(rs.getDouble("price"));
            booking.setStatus(PaymentStatus.fromInteger(rs.getInt("status")));
            booking.setVnpayData(rs.getString("vnpay_data"));
            bookings.add(booking);
        }

        rs.close();
        ps.close();
        con.close();

        return bookings;
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

    public void approveRefund(int booking_id) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        // Use a placeholder for both status and booking_id
        String query = "UPDATE booking SET status = 8 WHERE booking_id = ?";
        PreparedStatement ps = con.prepareStatement(query);

        // Set the dynamic status and booking_id
        // Assuming `status` is stored as a String in the database
        ps.setInt(1, booking_id); // Set the booking ID

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Refund status for booking ID " + booking_id + " has been updated to.");
        } else {
            System.out.println("No refund record found for booking ID " + booking_id + ".");
        }

        ps.close();
        con.close();
    }

}
