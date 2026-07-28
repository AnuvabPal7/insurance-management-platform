package com.insurance.management.controller;

import com.insurance.management.dto.*;
import com.insurance.management.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.create(request));
    }

    @PatchMapping("/{id}/settle")
    public ResponseEntity<PaymentResponse> settle(
            @PathVariable Long id, @Valid @RequestBody PaymentSettleRequest request) {
        return ResponseEntity.ok(paymentService.settle(id, request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(Authentication auth) {
        return ResponseEntity.ok(paymentService.getMyPayments(auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<PaymentResponse>> getOverdue() {
        return ResponseEntity.ok(paymentService.getOverdue());
    }

    @GetMapping("/policy/{policyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<PaymentResponse>> getByPolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(paymentService.getByPolicyId(policyId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}