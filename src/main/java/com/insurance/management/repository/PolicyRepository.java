package com.insurance.management.repository;

import com.insurance.management.entity.Policy;
import com.insurance.management.entity.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByCustomerId(Long customerId);
    List<Policy> findByCustomerUserEmail(String email);
    List<Policy> findByStatus(PolicyStatus status);
    Optional<Policy> findByPolicyNumber(String policyNumber);
}