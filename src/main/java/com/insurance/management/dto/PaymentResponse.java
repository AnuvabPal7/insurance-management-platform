package com.insurance.management.dto;

import com.insurance.management.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private String receiptNumber;
    private Long policyId;
    private String policyNumber;
    private String customerName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDate paidOn;
    private PaymentStatus status;
    private String paymentMethod;
}