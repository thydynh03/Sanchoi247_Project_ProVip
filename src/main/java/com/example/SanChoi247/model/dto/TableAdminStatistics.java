package com.example.SanChoi247.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class TableAdminStatistics {
    private String stadiumName;
    private String ownerName;
    private double revenue;
}
