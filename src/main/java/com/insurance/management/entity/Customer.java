package com.insurance.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    private String address;

    private String city;

    private String state;

    private String pincode;

    @Column(nullable = false, updatable = false)
    private LocalDate registeredOn;

    @PrePersist
    protected void onCreate() {
        registeredOn = LocalDate.now();
    }
}