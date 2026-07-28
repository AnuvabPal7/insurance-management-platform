package com.insurance.management.dto;

import com.insurance.management.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ClaimReviewRequest {

    @NotNull(message = "Status is required")
    private ClaimStatus status;

    private BigDecimal approvedAmount;

    private String remarks;
}