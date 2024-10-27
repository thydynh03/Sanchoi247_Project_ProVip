package com.example.SanChoi247.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.example.SanChoi247.model.entity.Message;

@Service
public class MessageService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper để lấy dữ liệu từ bảng messages đã lưu sẵn sender_name và sender_avatar
    private static final RowMapper<Message> messageRowMapper = (rs, rowNum) -> {
        Message message = new Message();
        message.setContent(rs.getString("content"));
        message.setSenderUid(rs.getInt("sender_uid"));
        message.setReceiverUid(rs.getObject("receiver_uid") != null ? rs.getInt("receiver_uid") : null);
        message.setTimestamp(rs.getTimestamp("timestamp"));
        
        // Lấy trực tiếp sender_name và sender_avatar từ bảng messages
        message.setSenderName(rs.getString("sender_name"));
        message.setSenderAvatar(rs.getString("sender_avatar"));

        return message;
    };

    // Lấy tất cả tin nhắn từ bảng messages
    public List<Message> getAllMessages() {
        String sql = "SELECT * FROM messages ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, messageRowMapper);
    }

    // Lưu tin nhắn vào cơ sở dữ liệu với sender_name và sender_avatar
    public void saveMessage(Message message) {
        // Truy vấn để lấy tên và avatar từ bảng users
        String userQuery = "SELECT name, avatar FROM users WHERE uid = ?";
        Map<String, Object> userInfo = jdbcTemplate.queryForMap(userQuery, message.getSenderUid());

        String senderName = (String) userInfo.get("name");
        String senderAvatar = (String) userInfo.get("avatar");

        // Lưu tin nhắn với cả sender_name và sender_avatar
        String insertMessageQuery = "INSERT INTO messages (sender_uid, receiver_uid, content, sender_name, sender_avatar, timestamp) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertMessageQuery, 
                            message.getSenderUid(), 
                            message.getReceiverUid(), 
                            message.getContent(), 
                            senderName, 
                            senderAvatar, 
                            new Timestamp(System.currentTimeMillis()));
    }
}