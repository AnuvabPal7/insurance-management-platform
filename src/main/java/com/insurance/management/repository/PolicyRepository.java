package com.insurance.management.repository;

import com.insurance.management.entity.Policy;
import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.entity.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByCustomerId(Long customerId);
    List<Policy> findByCustomerUserEmail(String email);
    List<Policy> findByStatus(PolicyStatus status);
    Optional<Policy> findByPolicyNumber(String policyNumber);

    Page<Policy> findByStatusAndPolicyType(PolicyStatus status, PolicyType policyType, Pageable pageable);
    Page<Policy> findByStatus(PolicyStatus status, Pageable pageable);
    Page<Policy> findByPolicyType(PolicyType policyType, Pageable pageable);
    Page<Policy> findByPolicyNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Policy> findAll(Pageable pageable);
}