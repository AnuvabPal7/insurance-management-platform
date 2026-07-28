package com.insurance.management.dto;

import com.insurance.management.entity.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class ClaimResponse {
    private Long id;
    private String claimNumber;
    private Long policyId;
    private String policyNumber;
    private String customerName;
    private String reason;
    private String description;
    private BigDecimal claimAmount;
    private BigDecimal approvedAmount;
    private ClaimStatus status;
    private LocalDate filedOn;
    private LocalDate resolvedOn;
    private String remarks;
}