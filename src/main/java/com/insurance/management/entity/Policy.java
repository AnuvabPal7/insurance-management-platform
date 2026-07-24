package com.insurance.management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType policyType;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private BigDecimal coverageAmount;

    @Column(nullable = false)
    private BigDecimal premiumAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDate createdOn;

    @PrePersist
    protected void onCreate() {
        createdOn = LocalDate.now();
        if (policyNumber == null) {
            policyNumber = "POL-" + System.currentTimeMillis();
        }
        if (status == null) {
            status = PolicyStatus.PENDING;
        }
    }
}