package com.insurance.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDate registeredOn;
}