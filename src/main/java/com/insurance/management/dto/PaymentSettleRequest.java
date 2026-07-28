package com.insurance.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentSettleRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}