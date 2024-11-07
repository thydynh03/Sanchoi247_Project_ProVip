package com.example.SanChoi247.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Rating {
    private int rating_id;
    private int star;
    private int uid; // Lưu UID của người dùng
    private int booking_id; // Lưu ID của đặt chỗ
}
