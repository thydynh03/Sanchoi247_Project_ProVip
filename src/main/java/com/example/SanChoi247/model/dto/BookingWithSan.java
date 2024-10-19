package com.example.SanChoi247.model.dto;

import com.example.SanChoi247.model.entity.Booking;
import com.example.SanChoi247.model.entity.San;
import lombok.*;

@Data
public class BookingWithSan {
    private Booking booking;
    private San san;
}
