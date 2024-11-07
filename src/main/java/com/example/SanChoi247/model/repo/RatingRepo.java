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
            ps.setInt(2, rating.getUid());
            ps.setInt(3, rating.getBooking_id());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exception
        }
    }

    public Double getAverageRating() {
        String sql = "SELECT AVG(star) FROM rating";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            var rs = ps.executeQuery();
            if (rs.next()) {
                double average = rs.getDouble(1);
                System.out.println("Average rating retrieved from database: " + average);
                return average;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Trả về 0.0 nếu không có kết quả
    }
}
