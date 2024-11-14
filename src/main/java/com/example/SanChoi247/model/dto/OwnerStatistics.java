package com.example.SanChoi247.model.dto;

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
public class OwnerStatistics {
    private double totalAmountStadiums;
    private int totalRefundBooking;
    private int totalRequestRefund;
}