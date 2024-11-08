package com.example.SanChoi247.model.entity;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ScheduleBooking {
    private int booking_id;
    private San san;
    private LocalTime start_time; // Use LocalTime instead of LocalDateTime
    private LocalTime end_time;   // Use LocalTime instead of LocalDateTime
    private String status; // 'booked', 'available'
    private float price;
    private LocalDate booking_date;
    
}
