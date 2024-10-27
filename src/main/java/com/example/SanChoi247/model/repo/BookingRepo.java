package com.example.SanChoi247.model.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
import com.example.SanChoi247.model.entity.Booking.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Repository
public class BookingRepo {
    @Autowired
    SanRepo sanRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    ScheduleBookingRepo scheduleBookingRepo;

    public int pushBooking(Booking booking) throws Exception {
        Class.forName(Baseconnection.nameClass); // Load the database driver

        String query = "INSERT INTO booking (date, uid, san_id, slot, price, status, vnpay_data) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            ps.setString(7, booking.getVnpayData());

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

                User user = userRepo.getUserById(rs.getInt("uid"));
                booking.setUser(user);

                // Create and set the San object
                San san = sanRepo.getSanById(rs.getInt("san_id"));

                booking.setSan(san);

                // Create and set the ScheduleBooking object
                ScheduleBooking scheduleBooking = scheduleBookingRepo.findById(rs.getInt("slot"));

                booking.setScheduleBooking(scheduleBooking);

                booking.setTotalprice(rs.getDouble("price"));
                booking.setStatus(Booking.PaymentStatus.fromInteger(rs.getInt("status")));
                booking.setVnpayData(rs.getString("vnpay_data"));

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

        String query = "UPDATE booking SET status = ?, vnpay_data = ? WHERE booking_id = ?";

        try (Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
                PreparedStatement ps = con.prepareStatement(query)) {

            // Gán giá trị cho các tham số trong câu lệnh SQL
            ps.setInt(1, booking.getStatus().toInteger()); // status
            ps.setString(2, booking.getVnpayData()); // vnpay_data
            ps.setInt(3, booking.getBooking_id()); // booking_id

            // Thực hiện câu lệnh UPDATE
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error updating booking", e);
        }
    }

    public ArrayList<Booking> getAllBookingByUserId(int uid) throws Exception {
        ArrayList<Booking> BookingList = new ArrayList<>();
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username,
                Baseconnection.password);
        PreparedStatement ps = con.prepareStatement(
                "SELECT booking_id, date, uid, san_id, slot, price, status, vnpay_data FROM Booking WHERE uid = ?");
        ps.setInt(1, uid);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int booking_id = rs.getInt("booking_id");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
            int idUser = rs.getInt("uid");
            int san_id = rs.getInt("san_id");
            San san = sanRepo.getSanById(san_id);
            int slot = rs.getInt("slot"); // Truy xuất Sbooking_id đúng cách
            ScheduleBooking scheduleBooking = scheduleBookingRepo.getScheduleBookingById(slot);
            User user = userRepo.getUserById(idUser);
            double totalprice = rs.getDouble("price");
            PaymentStatus status = PaymentStatus.fromInteger(rs.getInt("status"));
            String vnpayData = rs.getString("vnpay_data");

            Booking booking = new Booking(booking_id, date, user, san, scheduleBooking, totalprice, status, vnpayData);
            BookingList.add(booking);
        }

        rs.close();
        ps.close();
        con.close();

        return BookingList;
    }

}
