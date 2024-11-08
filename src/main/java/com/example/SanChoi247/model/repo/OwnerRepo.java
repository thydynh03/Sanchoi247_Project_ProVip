package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.dto.OwnerStatistics;
import com.example.SanChoi247.model.dto.TableOwnerStatistics;
import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.Booking.PaymentStatus;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;

@Repository
public class OwnerRepo {
    @Autowired
    SanRepo sanRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;
    @Autowired
    UserRepo userRepo;

    public OwnerStatistics getStatisticsForOwner(int uid) throws Exception {
        Class.forName(Baseconnection.nameClass);
        OwnerStatistics stats = new OwnerStatistics();

        String sql = "SELECT " +
                "(SELECT SUM(b.price) " +
                " FROM booking b " +
                " JOIN san s ON b.san_id = s.san_id " +
                " WHERE b.status = 0 AND s.uid = ?) AS total_amount_stadiums, " +

                "(SELECT COUNT(*) " +
                " FROM booking b " +
                " JOIN san s ON b.san_id = s.san_id " +
                " WHERE (b.status = 3 OR b.status = 4) AND s.uid = ?) AS total_refund_booking, " +

                "(SELECT COUNT(*) " +
                " FROM booking b " +
                " JOIN san s ON b.san_id = s.san_id " +
                " WHERE (b.status = 5 OR b.status = 6) AND s.uid = ?) AS total_request_refund";

        ;

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {

            // Thiết lập tham số cho owner_id
            ps.setInt(1, uid);
            ps.setInt(2, uid);
            ps.setInt(3, uid);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalAmountStadiums(rs.getDouble("total_amount_stadiums"));
                    stats.setTotalRefundBooking(rs.getInt("total_refund_booking"));
                    stats.setTotalRequestRefund(rs.getInt("total_request_refund"));
                }
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

    public List<Double> getMonthlyRevenueForOwner(int uid) throws Exception {
        List<Double> monthlyRevenue = new ArrayList<>(Collections.nCopies(12, 0.0)); // Khởi tạo danh sách doanh thu với
                                                                                     // 12 phần tử bằng 0.0
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String sql = "SELECT MONTH(b.date) AS month, SUM(b.price) AS monthly_revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE b.status = 0 AND s.uid = ? AND YEAR(b.date) = ? " +
                "GROUP BY MONTH(b.date) " +
                "ORDER BY MONTH(b.date);";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, uid); // Thiết lập tham số cho owner_id
            ps.setInt(2, currentYear); // Thiết lập năm hiện tại

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("month") - 1; // Tháng trong SQL bắt đầu từ 1, cần giảm 1 để phù hợp với danh
                                                        // sách 0-based
                    double revenue = rs.getDouble("monthly_revenue");

                    // Đặt doanh thu vào đúng vị trí trong danh sách
                    monthlyRevenue.set(month, revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving monthly revenue for owner", e);
        }
        return monthlyRevenue;
    }

    public List<Double> getDailyRevenueForOwner(int uid) throws Exception {
        List<Double> dailyRevenue = new ArrayList<>(Collections.nCopies(31, 0.0)); // Initialize a list with 31 entries
                                                                                   // for each day in a month, all set
                                                                                   // to 0.0
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Months are 0-based in Calendar, so add 1

        String sql = "SELECT DAY(b.date) AS day, SUM(b.price) AS daily_revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE b.status = 0 AND s.uid = ? AND YEAR(b.date) = ? AND MONTH(b.date) = ? " +
                "GROUP BY DAY(b.date) " +
                "ORDER BY DAY(b.date);";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, uid); // Set the owner ID
            ps.setInt(2, currentYear); // Set the current year
            ps.setInt(3, currentMonth); // Set the current month

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("day") - 1; // SQL days start at 1, so subtract 1 for a 0-based list
                    double revenue = rs.getDouble("daily_revenue");

                    // Place the revenue in the correct position in the list
                    dailyRevenue.set(day, revenue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error retrieving daily revenue for owner", e);
        }

        return dailyRevenue;
    }

    public List<TableOwnerStatistics> getFieldRevenues(int ownerId) throws Exception {
        Class.forName(Baseconnection.nameClass);
        List<TableOwnerStatistics> fieldRevenues = new ArrayList<>();

        String sql = "SELECT u.ten_san AS stadium_name, u.username AS owner_name, SUM(b.price) AS revenue " +
                "FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "JOIN users u ON s.uid = u.uid " +
                "WHERE b.status = 0 AND u.uid = ? " + // Lọc theo owner_id
                "GROUP BY u.ten_san, u.username " +
                "ORDER BY revenue DESC";
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId); // Thiết lập ownerId

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TableOwnerStatistics stats = new TableOwnerStatistics();
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

    public ArrayList<Booking> getAllRequestRefund(int uid) throws Exception {
        ArrayList<Booking> bookings = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        String query = "SELECT * FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE (b.status = 5 OR b.status = 6) AND s.uid = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, uid);
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

    public ArrayList<Booking> getAllRequestRefundForAdmin() throws Exception {
        ArrayList<Booking> bookings = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        String query = "SELECT * FROM booking WHERE status = 7";
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

    public ArrayList<Booking> getAllUserBooking(int uid) throws Exception {
        ArrayList<Booking> bookings = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        String query = "SELECT * FROM booking b " +
                "JOIN san s ON b.san_id = s.san_id " +
                "WHERE (b.status = 0) AND s.uid = ?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, uid);
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
}
