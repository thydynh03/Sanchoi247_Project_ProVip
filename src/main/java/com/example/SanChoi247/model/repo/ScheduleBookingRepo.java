package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.LoaiSan;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.Size;
import com.example.SanChoi247.model.entity.User;

@Repository
public class ScheduleBookingRepo {
    @Autowired
    SanRepo sanRepo;

    public List<ScheduleBooking> getAvailableBookings(int sanId) throws Exception {
        Class.forName(Baseconnection.nameClass);
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password)) {
            // Chỉ định câu truy vấn SQL với điều kiện san_id
            PreparedStatement ps = con.prepareStatement("SELECT * FROM Schedulebooking WHERE san_id = ?");
            ps.setInt(1, sanId);

            ResultSet rs = ps.executeQuery();
            List<ScheduleBooking> bookings = new ArrayList<>();

            // Lấy San một lần trước khi vào vòng lặp
            San san = sanRepo.getSanById(sanId);

            while (rs.next()) {
                // Chỉ cần lấy những thông tin cần thiết
                int booking_id = rs.getInt("Sbooking_id");
                LocalTime start_time = rs.getTime("start_time").toLocalTime();
                LocalTime end_time = rs.getTime("end_time").toLocalTime();
                String status = rs.getString("status");
                float price = rs.getFloat("price");
                LocalDate booking_date = rs.getDate("booking_date").toLocalDate();

                // Tạo đối tượng ScheduleBooking
                ScheduleBooking booking = new ScheduleBooking(booking_id, san, start_time, end_time, status, price,
                        booking_date);
                bookings.add(booking);
            }

            rs.close();
            ps.close();
            return bookings;
        }
    }

    public List<ScheduleBooking> getAvailableBookingsByDate(int sanId, LocalDate bookingDate) throws Exception {
        Class.forName(Baseconnection.nameClass);
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password)) {
            // Update SQL query to filter by san_id and booking_date
            PreparedStatement ps = con
                    .prepareStatement("SELECT * FROM Schedulebooking WHERE san_id = ? AND booking_date = ?");
            ps.setInt(1, sanId);
            ps.setDate(2, java.sql.Date.valueOf(bookingDate)); // Convert LocalDate to java.sql.Date

            ResultSet rs = ps.executeQuery();
            List<ScheduleBooking> bookings = new ArrayList<>();

            while (rs.next()) {
                int booking_id = rs.getInt("Sbooking_id");
                LocalTime start_time = rs.getTime("start_time").toLocalTime(); // Fetch start_time
                LocalTime end_time = rs.getTime("end_time").toLocalTime(); // Fetch end_time
                String status = rs.getString("status");
                float price = rs.getFloat("price");
                LocalDate booking_date = rs.getDate("booking_date").toLocalDate();
                San san = sanRepo.getSanById(sanId);
                ScheduleBooking booking = new ScheduleBooking(booking_id, san, start_time, end_time, status, price,
                        booking_date);
                bookings.add(booking);
            }

            rs.close();
            ps.close();
            return bookings;
        }
    }

    public void updateScheduleBookingStatus(int sbid, String status) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver
        String query = "UPDATE Schedulebooking SET status = ? WHERE Sbooking_id = ?";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setInt(2, sbid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating schedule booking status", e);
        }
    }

    public ScheduleBooking findById(int bookingId) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password)) {
            // SQL query to find a booking by ID
            String query = "SELECT * FROM Schedulebooking WHERE Sbooking_id = ?";
            ScheduleBooking booking = null;

            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, bookingId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int booking_id = rs.getInt("Sbooking_id");
                    LocalTime start_time = rs.getTime("start_time").toLocalTime(); // Fetch start_time
                    LocalTime end_time = rs.getTime("end_time").toLocalTime(); // Fetch end_time
                    String status = rs.getString("status");
                    float price = rs.getFloat("price");
                    LocalDate booking_date = rs.getDate("booking_date").toLocalDate();
                    San san = sanRepo.getSanById(rs.getInt("san_id")); // Assuming you have a San repo to fetch related
                                                                       // data

                    booking = new ScheduleBooking(booking_id, san, start_time, end_time, status, price, booking_date);
                }

                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new Exception("Error fetching booking by ID", e);
            }

            return booking;
        }
    }

    public void update(ScheduleBooking booking) throws Exception {
        // Database connection and query
        String query = "UPDATE Schedulebooking SET start_time = ?, end_time = ?, status = ?, price = ?, booking_date = ? WHERE Sbooking_id = ?";
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password); // Establish connection
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setTime(1, java.sql.Time.valueOf(booking.getStart_time())); // Convert LocalTime to Time
            ps.setTime(2, java.sql.Time.valueOf(booking.getEnd_time())); // Convert LocalTime to Time
            ps.setString(3, booking.getStatus());
            ps.setFloat(4, booking.getPrice());
            ps.setDate(5, java.sql.Date.valueOf(booking.getBooking_date())); // If needed
            ps.setInt(6, booking.getBooking_id()); // Set booking ID for WHERE clause

            ps.executeUpdate();
            System.out.println("Booking updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error updating booking", e);
        } finally {
            con.close();
        }
    }

    public void addScheduleBooking(ScheduleBooking booking) throws Exception {
        // Query to insert new booking
        String query = "INSERT INTO Schedulebooking (san_id, start_time, end_time, status, price, booking_date) VALUES (?,?, ?, ?, ?, ?)";

        // Establish database connection
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);

        try (PreparedStatement ps = con.prepareStatement(query)) {
            // Set parameters for the insert query
            ps.setInt(1, booking.getSan().getSan_id());
            ps.setTime(2, java.sql.Time.valueOf(booking.getStart_time())); // Convert LocalTime to Time
            ps.setTime(3, java.sql.Time.valueOf(booking.getEnd_time())); // Convert LocalTime to Time
            ps.setString(4, "available");
            ps.setFloat(5, booking.getPrice());
            ps.setDate(6, java.sql.Date.valueOf(booking.getBooking_date())); // Convert LocalDate to Date

            // Execute insert query
            ps.executeUpdate();
            System.out.println("Booking added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error adding new booking", e);
        } finally {
            con.close(); // Close the connection
        }
    }

    public boolean existsBooking(LocalDate bookingDate, LocalTime startTime, LocalTime endTime, int sanId) {
        String query = "SELECT COUNT(*) FROM Schedulebooking WHERE booking_date = ? AND start_time = ? AND end_time = ? AND san_id = ?";

        // Khai báo biến để lưu số lượng booking tìm thấy
        int count = 0;

        // Establish database connection
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {

            // Set parameters for the query
            ps.setDate(1, java.sql.Date.valueOf(bookingDate)); // Convert LocalDate to Date
            ps.setTime(2, java.sql.Time.valueOf(startTime)); // Convert LocalTime to Time
            ps.setTime(3, java.sql.Time.valueOf(endTime)); // Convert LocalTime to Time
            ps.setInt(4, sanId); // Set sanId

            // Execute the query and retrieve the result
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1); // Lấy giá trị COUNT(*) từ kết quả
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Trả về true nếu tìm thấy booking, ngược lại trả về false
        return count > 0;
    }

    public ScheduleBooking getScheduleBookingById(int Sbooking_id) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Schedulebooking WHERE Sbooking_id = ?");
        ps.setInt(1, Sbooking_id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) { // Check if there's a result
            int Sbooking_id1 = rs.getInt("Sbooking_id");
            int san_id = rs.getInt("san_id");
            San san = sanRepo.getSanById(san_id);
            LocalTime start_time = rs.getTime("start_time").toLocalTime(); // Fetch start_time
            LocalTime end_time = rs.getTime("end_time").toLocalTime(); // Fetch end_time
            String status = rs.getString("status");
            float price = rs.getFloat("price");
            LocalDate booking_date = rs.getDate("booking_date").toLocalDate(); // Fetch booking_date

            ScheduleBooking scheduleBooking = new ScheduleBooking(Sbooking_id1, san, start_time, end_time, status,
                    price, booking_date);
            rs.close();
            ps.close();
            con.close();
            return scheduleBooking;
        } else {
            rs.close();
            ps.close();
            con.close();
            throw new Exception("No schedule booking found with id: " + Sbooking_id);
        }
    }

    public List<ScheduleBooking> findBySanAndDate(int sanId, LocalDate bookingDate) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver

        // Tạo kết nối đến cơ sở dữ liệu
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password)) {

            // Câu truy vấn SQL để lấy các lịch đặt sân dựa trên `san_id` và `booking_date`
            String query = "SELECT * FROM Schedulebooking WHERE san_id = ? AND booking_date = ?";
            List<ScheduleBooking> bookings = new ArrayList<>();

            // Chuẩn bị câu lệnh SQL và thiết lập các tham số
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, sanId);
                ps.setDate(2, java.sql.Date.valueOf(bookingDate)); // Chuyển đổi LocalDate sang java.sql.Date

                // Thực thi truy vấn và lấy kết quả
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int booking_id = rs.getInt("Sbooking_id");
                        LocalTime start_time = rs.getTime("start_time").toLocalTime();
                        LocalTime end_time = rs.getTime("end_time").toLocalTime();
                        String status = rs.getString("status");
                        float price = rs.getFloat("price");
                        LocalDate booking_date = rs.getDate("booking_date").toLocalDate();
                        San san = sanRepo.getSanById(sanId);

                        // Tạo đối tượng ScheduleBooking và thêm vào danh sách
                        ScheduleBooking booking = new ScheduleBooking(booking_id, san, start_time, end_time, status,
                                price,
                                booking_date);
                        bookings.add(booking);
                    }
                }
            }

            // Trả về danh sách các booking tìm được
            return bookings;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Error finding schedule bookings by sanId and date", e);
        }
    }

    // public List<ScheduleBooking> findBySanAndDate(int sanId, LocalDate
    // bookingDate) throws Exception {
    // Class.forName(Baseconnection.nameClass);
    // try (Connection con = DriverManager.getConnection(Baseconnection.url,
    // Baseconnection.username,
    // Baseconnection.password)) {
    // String query = "SELECT * FROM Schedulebooking WHERE san_id = ? AND
    // booking_date = ?";
    // PreparedStatement ps = con.prepareStatement(query);
    // ps.setInt(1, sanId);
    // ps.setDate(2, java.sql.Date.valueOf(bookingDate));

    // ResultSet rs = ps.executeQuery();
    // List<ScheduleBooking> bookings = new ArrayList<>();
    // while (rs.next()) {
    // ScheduleBooking booking = new ScheduleBooking();
    // booking.setStart_time(rs.getTime("start_time").toLocalTime());
    // booking.setEnd_time(rs.getTime("end_time").toLocalTime());
    // booking.setStatus(rs.getString("status"));
    // bookings.add(booking);
    // }
    // return bookings;
    // }
    // }

}
