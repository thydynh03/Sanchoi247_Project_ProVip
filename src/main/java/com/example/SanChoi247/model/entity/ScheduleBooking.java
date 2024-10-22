package com.example.SanChoi247.model.entity;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleBooking {
    private int booking_id;
    private San san;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String status; // 'booked', 'available'
    private float price;
    
}
