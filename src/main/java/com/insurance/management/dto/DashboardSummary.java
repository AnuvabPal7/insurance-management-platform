package com.insurance.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class DashboardSummary {
    private long totalCustomers;
    private long totalPolicies;
    private long activePolicies;
    private long totalClaims;
    private Map<String, Long> claimsByStatus;
    private BigDecimal totalPremiumCollected;
    private BigDecimal totalClaimAmountApproved;
    private long pendingPayments;
    private long overduePayments;
}