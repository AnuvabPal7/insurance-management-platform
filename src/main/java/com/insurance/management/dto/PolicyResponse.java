package com.insurance.management.dto;

import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.entity.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class PolicyResponse {
    private Long id;
    private String policyNumber;
    private Long customerId;
    private String customerName;
    private PolicyType policyType;
    private String policyName;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PolicyStatus status;
    private LocalDate createdOn;
}