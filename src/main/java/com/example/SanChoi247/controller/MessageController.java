package com.example.SanChoi247.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SanChoi247.model.entity.Message;
import com.example.SanChoi247.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Lấy danh sách tin nhắn theo UID người gửi hoặc người nhận
    @GetMapping
    public List<Message> getMessages(
        @RequestParam(value = "senderUid", required = false) Integer senderUid,
        @RequestParam(value = "receiverUid", required = false) Integer receiverUid) {
    if (senderUid != null) {
        return messageService.getMessagesBySenderUid(senderUid);
    } else if (receiverUid != null) {
        return messageService.getMessagesByReceiverUid(receiverUid);
    } else {
        throw new IllegalArgumentException("Either senderUid or receiverUid must be provided.");
    }
}

    // Lưu tin nhắn vào database
    @PostMapping
    public void saveMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
    }
}
