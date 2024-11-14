package com.example.SanChoi247.model.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.SanChoi247.model.entity.Rating;

@Repository
public class RatingRepo {

    @Autowired
    private DataSource dataSource;

    public void save(Rating rating) {
        String sql = "INSERT INTO rating (star, uid, booking_id) VALUES (?, ?, ?)";
    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rating.getStar());
            ps.setInt(2, rating.getUser().getUid());  // Fetches uid from the User object
            ps.setInt(3, rating.getBooking().getBooking_id());  // Fetches booking_id from the Booking object
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exception
        }
    }
    

    
}
