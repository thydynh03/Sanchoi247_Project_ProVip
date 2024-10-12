package com.example.SanChoi247.model.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

@Repository
public class BookingRepo {

    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;

    public int pushBooking(Booking booking) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver
    
        String query = "INSERT INTO booking (date, uid, san_id, slot, price, status) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
             PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            // Gán giá trị cho các tham số trong câu lệnh SQL
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(booking.getDate())); // date
            ps.setInt(2, booking.getUser().getUid()); // uid
            ps.setInt(3, booking.getSan().getSan_id()); // san_id
            ps.setInt(4, booking.getScheduleBooking().getBooking_id()); // slot
            ps.setDouble(5, booking.getTotalprice()); // price
            ps.setInt(6, booking.getStatus().toInteger()); // status
    
            // Thực hiện câu lệnh INSERT
            int affectedRows = ps.executeUpdate();
    
            // Kiểm tra xem câu lệnh có thực hiện thành công không
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Lấy booking_id vừa tạo từ database
                        int bookingId = generatedKeys.getInt(1);
                        return bookingId; // Trả về booking_id
                    } else {
                        throw new Exception("Creating booking failed, no ID obtained.");
                    }
                }
            } else {
                throw new Exception("Creating booking failed, no rows affected.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error inserting booking", e);
        }
    }
    

    public Booking findById(int bookingId) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver

        String query = "SELECT * FROM booking WHERE booking_id = ?";
        Booking booking = null;

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {

            // Set the parameter for the query (booking ID)
            ps.setInt(1, bookingId);

            // Execute the query
            ResultSet rs = ps.executeQuery();

            // If a result is found
            if (rs.next()) {
                booking = new Booking();
                booking.setBooking_id(rs.getInt("booking_id"));
                booking.setDate(rs.getTimestamp("date").toLocalDateTime());

                // Create and set the User object
                User user = new User();
                user.setUid(rs.getInt("uid"));
                booking.setUser(user);

                // Create and set the San object
                San san = new San();
                san.setSan_id(rs.getInt("san_id"));
                booking.setSan(san);

                // Create and set the ScheduleBooking object
                ScheduleBooking scheduleBooking = new ScheduleBooking();
                scheduleBooking.setBooking_id(rs.getInt("slot"));
                booking.setScheduleBooking(scheduleBooking);

                booking.setTotalprice(rs.getDouble("price"));
                booking.setStatus(Booking.PaymentStatus.fromInteger(rs.getInt("status")));

                // Optionally: Set the VNPay data if needed
                // booking.setVnpayData(rs.getString("vnpay_data"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error finding booking by ID", e);
        }

        return booking;
    }
    public void save(Booking booking) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver
    
        String query = "UPDATE booking SET status = ? WHERE booking_id = ?";
    
        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {
    
            // Gán giá trị cho các tham số trong câu lệnh SQL
            ps.setInt(1, booking.getStatus().toInteger()); // status
            ps.setInt(2, booking.getBooking_id()); // booking_id
    
            // Thực hiện câu lệnh UPDATE
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating booking status", e);
        }
    }
    
    
}
