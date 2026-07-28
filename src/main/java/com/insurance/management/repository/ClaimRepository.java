package com.insurance.management.repository;

import com.insurance.management.entity.Claim;
import com.insurance.management.entity.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByPolicyId(Long policyId);
    List<Claim> findByPolicyCustomerUserEmail(String email);
    List<Claim> findByStatus(ClaimStatus status);
    Optional<Claim> findByClaimNumber(String claimNumber);

    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);
    Page<Claim> findByReasonContainingIgnoreCase(String keyword, Pageable pageable);
}