package com.insurance.management.service;

import com.insurance.management.dto.DashboardSummary;
import com.insurance.management.entity.ClaimStatus;
import com.insurance.management.entity.PaymentStatus;
import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final PaymentRepository paymentRepository;

    public DashboardSummary getSummary() {
        long totalCustomers = customerRepository.count();
        long totalPolicies = policyRepository.count();
        long activePolicies = policyRepository.findByStatus(PolicyStatus.ACTIVE).size();

        List<com.insurance.management.entity.Claim> allClaims = claimRepository.findAll();
        long totalClaims = allClaims.size();

        Map<String, Long> claimsByStatus = Arrays.stream(ClaimStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        status -> claimRepository.findByStatus(status).stream().count()
                ));

        BigDecimal totalPremiumCollected = paymentRepository.findByStatus(PaymentStatus.PAID).stream()
                .map(com.insurance.management.entity.Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalClaimAmountApproved = allClaims.stream()
                .filter(c -> c.getApprovedAmount() != null)
                .map(com.insurance.management.entity.Claim::getApprovedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingPayments = paymentRepository.findByStatus(PaymentStatus.PENDING).size();
        long overduePayments = paymentRepository.findByStatus(PaymentStatus.OVERDUE).size();

        return DashboardSummary.builder()
                .totalCustomers(totalCustomers)
                .totalPolicies(totalPolicies)
                .activePolicies(activePolicies)
                .totalClaims(totalClaims)
                .claimsByStatus(claimsByStatus)
                .totalPremiumCollected(totalPremiumCollected)
                .totalClaimAmountApproved(totalClaimAmountApproved)
                .pendingPayments(pendingPayments)
                .overduePayments(overduePayments)
                .build();
    }
}