package com.insurance.management.repository;

import com.insurance.management.entity.Payment;
import com.insurance.management.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPolicyId(Long policyId);
    List<Payment> findByPolicyCustomerUserEmail(String email);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByReceiptNumber(String receiptNumber);
}