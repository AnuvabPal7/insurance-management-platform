package com.insurance.management.service;

import com.insurance.management.dto.*;
import com.insurance.management.entity.Claim;
import com.insurance.management.entity.ClaimStatus;
import com.insurance.management.entity.Policy;
import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.exception.ApiException;
import com.insurance.management.repository.ClaimRepository;
import com.insurance.management.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimResponse fileClaim(String userEmail, ClaimRequest request) {
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));

        if (!policy.getCustomer().getUser().getEmail().equals(userEmail)) {
            throw new ApiException("You can only file claims for your own policies", HttpStatus.FORBIDDEN);
        }

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new ApiException("Cannot file a claim on a non-active policy", HttpStatus.BAD_REQUEST);
        }

        if (request.getClaimAmount().compareTo(policy.getCoverageAmount()) > 0) {
            throw new ApiException("Claim amount exceeds policy coverage", HttpStatus.BAD_REQUEST);
        }

        Claim claim = Claim.builder()
                .policy(policy)
                .reason(request.getReason())
                .description(request.getDescription())
                .claimAmount(request.getClaimAmount())
                .status(ClaimStatus.SUBMITTED)
                .build();

        Claim saved = claimRepository.save(claim);
        return toResponse(saved);
    }

    public List<ClaimResponse> getMyClaims(String userEmail) {
        return claimRepository.findByPolicyCustomerUserEmail(userEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClaimResponse> getByPolicyId(Long policyId) {
        return claimRepository.findByPolicyId(policyId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClaimResponse> getAll() {
        return claimRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ClaimResponse getById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ApiException("Claim not found", HttpStatus.NOT_FOUND));
        return toResponse(claim);
    }

    public ClaimResponse review(Long id, ClaimReviewRequest request) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ApiException("Claim not found", HttpStatus.NOT_FOUND));

        claim.setStatus(request.getStatus());
        claim.setRemarks(request.getRemarks());

        if (request.getStatus() == ClaimStatus.APPROVED || request.getStatus() == ClaimStatus.SETTLED) {
            claim.setApprovedAmount(
                    request.getApprovedAmount() != null ? request.getApprovedAmount() : claim.getClaimAmount());
        }

        if (request.getStatus() == ClaimStatus.APPROVED || request.getStatus() == ClaimStatus.REJECTED
                || request.getStatus() == ClaimStatus.SETTLED) {
            claim.setResolvedOn(LocalDate.now());
        }

        Claim updated = claimRepository.save(claim);
        return toResponse(updated);
    }

    public void delete(Long id) {
        if (!claimRepository.existsById(id)) {
            throw new ApiException("Claim not found", HttpStatus.NOT_FOUND);
        }
        claimRepository.deleteById(id);
    }

    private ClaimResponse toResponse(Claim c) {
        return ClaimResponse.builder()
                .id(c.getId())
                .claimNumber(c.getClaimNumber())
                .policyId(c.getPolicy().getId())
                .policyNumber(c.getPolicy().getPolicyNumber())
                .customerName(c.getPolicy().getCustomer().getFullName())
                .reason(c.getReason())
                .description(c.getDescription())
                .claimAmount(c.getClaimAmount())
                .approvedAmount(c.getApprovedAmount())
                .status(c.getStatus())
                .filedOn(c.getFiledOn())
                .resolvedOn(c.getResolvedOn())
                .remarks(c.getRemarks())
                .build();
    }
}