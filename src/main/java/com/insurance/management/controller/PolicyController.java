package com.insurance.management.controller;

import com.insurance.management.dto.*;
import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PolicyResponse> create(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.ok(policyService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PolicyResponse>> getMyPolicies(Authentication auth) {
        return ResponseEntity.ok(policyService.getMyPolicies(auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<PolicyResponse>> getAll() {
        return ResponseEntity.ok(policyService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PolicyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<PolicyResponse>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(policyService.getByCustomerId(customerId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PolicyResponse> update(
            @PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.ok(policyService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<PolicyResponse> updateStatus(
            @PathVariable Long id, @RequestParam PolicyStatus status) {
        return ResponseEntity.ok(policyService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}