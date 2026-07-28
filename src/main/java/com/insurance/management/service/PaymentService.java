package com.insurance.management.service;

import com.insurance.management.dto.*;
import com.insurance.management.entity.Payment;
import com.insurance.management.entity.PaymentStatus;
import com.insurance.management.entity.Policy;
import com.insurance.management.exception.ApiException;
import com.insurance.management.repository.PaymentRepository;
import com.insurance.management.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PolicyRepository policyRepository;

    public PaymentResponse create(PaymentRequest request) {
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));

        Payment payment = Payment.builder()
                .policy(policy)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    public PaymentResponse settle(Long id, PaymentSettleRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new ApiException("Payment already settled", HttpStatus.CONFLICT);
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidOn(LocalDate.now());
        payment.setPaymentMethod(request.getPaymentMethod());

        Payment updated = paymentRepository.save(payment);
        return toResponse(updated);
    }

    public List<PaymentResponse> getMyPayments(String userEmail) {
        return paymentRepository.findByPolicyCustomerUserEmail(userEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> getByPolicyId(Long policyId) {
        return paymentRepository.findByPolicyId(policyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> getOverdue() {
        List<Payment> pending = paymentRepository.findByStatus(PaymentStatus.PENDING);
        LocalDate today = LocalDate.now();

        pending.stream()
                .filter(p -> p.getDueDate().isBefore(today))
                .forEach(p -> {
                    p.setStatus(PaymentStatus.OVERDUE);
                    paymentRepository.save(p);
                });

        return paymentRepository.findByStatus(PaymentStatus.OVERDUE).stream()
                .map(this::toResponse)
                .toList();
    }

    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ApiException("Payment not found", HttpStatus.NOT_FOUND);
        }
        paymentRepository.deleteById(id);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .receiptNumber(p.getReceiptNumber())
                .policyId(p.getPolicy().getId())
                .policyNumber(p.getPolicy().getPolicyNumber())
                .customerName(p.getPolicy().getCustomer().getFullName())
                .amount(p.getAmount())
                .dueDate(p.getDueDate())
                .paidOn(p.getPaidOn())
                .status(p.getStatus())
                .paymentMethod(p.getPaymentMethod())
                .build();
    }
}