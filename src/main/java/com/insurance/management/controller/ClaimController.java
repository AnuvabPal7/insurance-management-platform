package com.insurance.management.controller;

import com.insurance.management.dto.*;
import com.insurance.management.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping
    public ResponseEntity<ClaimResponse> fileClaim(
            @Valid @RequestBody ClaimRequest request, Authentication auth) {
        return ResponseEntity.ok(claimService.fileClaim(auth.getName(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ClaimResponse>> getMyClaims(Authentication auth) {
        return ResponseEntity.ok(claimService.getMyClaims(auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<ClaimResponse>> getAll() {
        return ResponseEntity.ok(claimService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ClaimResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getById(id));
    }

    @GetMapping("/policy/{policyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<List<ClaimResponse>> getByPolicy(@PathVariable Long policyId) {
        return ResponseEntity.ok(claimService.getByPolicyId(policyId));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    public ResponseEntity<ClaimResponse> review(
            @PathVariable Long id, @Valid @RequestBody ClaimReviewRequest request) {
        return ResponseEntity.ok(claimService.review(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        claimService.delete(id);
        return ResponseEntity.noContent().build();
    }
}