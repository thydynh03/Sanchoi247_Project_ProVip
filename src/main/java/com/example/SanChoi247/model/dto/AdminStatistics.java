package com.example.SanChoi247.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatistics {
    private double totalSoldAmountActiveStadiums;
    private int totalOngoingStadiums;
    private int totalApprovedStadiums;
    private int totalRejectedStadiums;
    private int totalRefundBooking; // Thêm thuộc tính này
    private int totalPassStadiums;
    private int totalInactiveOwners;
    private int totalActiveOwners;
    private int totalUsersExcludingOwners;
    private int totalBannedUsers;
}
