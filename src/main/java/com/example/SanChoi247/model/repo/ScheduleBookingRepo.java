package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.San;
import com.example.SanChoi247.model.entity.ScheduleBooking;
import com.example.SanChoi247.model.entity.User;
@Repository
public class ScheduleBookingRepo {
    @Autowired
    UserRepo userRepo;
    @Autowired
    SanRepo sanRepo;
    public List<ScheduleBooking> getAvailableBookings(int sanId) throws Exception {
        Class.forName(Baseconnection.nameClass);
        Connection con = DriverManager.getConnection(Baseconnection.url, Baseconnection.username, Baseconnection.password);
    
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Schedulebooking WHERE san_id = ?");
        ps.setInt(1, sanId);
        ResultSet rs = ps.executeQuery();
    
        List<ScheduleBooking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
    
        // Kiểm tra nếu có kết quả trả về từ ResultSet
        if (!rs.isBeforeFirst()) {
            // Nếu không có kết quả nào, return danh sách rỗng
            return bookings;
        }
    
        // Duyệt qua các kết quả và kiểm tra thời gian
        while (rs.next()) {
            int booking_id = rs.getInt("booking_id");
           
            
    
            LocalDateTime start_time = rs.getTimestamp("start_time").toLocalDateTime();
            LocalDateTime end_time = rs.getTimestamp("end_time").toLocalDateTime();
            String status = rs.getString("status");
            float price = rs.getFloat("price");
            // Kiểm tra nếu thời gian kết thúc đã qua, set lại trạng thái thành "available"
            // if (end_time.isBefore(now)) {
            //     status = "available";
            //     PreparedStatement updatePs = con.prepareStatement("UPDATE Schedulebooking SET status = ? WHERE booking_id = ?");
            //     updatePs.setString(1, "available");
            //     updatePs.setInt(2, booking_id);
            //     updatePs.executeUpdate();
            // }
            
            San san = sanRepo.getSanById(sanId);
            ScheduleBooking booking = new ScheduleBooking(booking_id, san, start_time, end_time, status,price);
            bookings.add(booking); // Thêm vào danh sách bookings
        }
    
        rs.close();  // Close ResultSet
        ps.close();  // Close PreparedStatement
        con.close(); // Close Connection
        
        return bookings;
    }
    
    


}
