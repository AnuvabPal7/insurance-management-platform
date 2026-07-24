package com.insurance.management.dto;

import com.insurance.management.entity.PolicyType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolicyRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Policy type is required")
    private PolicyType policyType;

    @NotBlank(message = "Policy name is required")
    private String policyName;

    @NotNull(message = "Coverage amount is required")
    @Positive(message = "Coverage amount must be positive")
    private BigDecimal coverageAmount;

    @NotNull(message = "Premium amount is required")
    @Positive(message = "Premium amount must be positive")
    private BigDecimal premiumAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
}