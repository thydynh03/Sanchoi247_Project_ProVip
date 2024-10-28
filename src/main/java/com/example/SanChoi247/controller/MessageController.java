package com.example.SanChoi247.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SanChoi247.model.entity.Message;
import com.example.SanChoi247.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Lấy tất cả tin nhắn trong cơ sở dữ liệu
    @GetMapping("/all")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages(); // Gọi phương thức trong service để lấy tất cả tin nhắn
        return ResponseEntity.ok(messages); // Trả về danh sách tin nhắn
    }

    // Lưu tin nhắn vào cơ sở dữ liệu
    @PostMapping
    public ResponseEntity<String> saveMessage(@RequestBody Message message) {
        try {
            messageService.saveMessage(message); // Lưu tin nhắn vào cơ sở dữ liệu
            return ResponseEntity.ok("Message saved successfully"); // Trả về thông báo thành công
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving message: " + e.getMessage()); // Trả về lỗi nếu có
        }
    }
}