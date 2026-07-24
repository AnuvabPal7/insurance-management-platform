package com.insurance.management.service;

import com.insurance.management.dto.*;
import com.insurance.management.entity.Customer;
import com.insurance.management.entity.Policy;
import com.insurance.management.entity.PolicyStatus;
import com.insurance.management.exception.ApiException;
import com.insurance.management.repository.CustomerRepository;
import com.insurance.management.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyResponse create(PolicyRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ApiException("End date must be after start date", HttpStatus.BAD_REQUEST);
        }

        Policy policy = Policy.builder()
                .customer(customer)
                .policyType(request.getPolicyType())
                .policyName(request.getPolicyName())
                .coverageAmount(request.getCoverageAmount())
                .premiumAmount(request.getPremiumAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(PolicyStatus.ACTIVE)
                .build();

        Policy saved = policyRepository.save(policy);
        return toResponse(saved);
    }

    public List<PolicyResponse> getMyPolicies(String userEmail) {
        return policyRepository.findByCustomerUserEmail(userEmail).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PolicyResponse> getByCustomerId(Long customerId) {
        return policyRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PolicyResponse> getAll() {
        return policyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PolicyResponse getById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));
        return toResponse(policy);
    }

    public PolicyResponse updateStatus(Long id, PolicyStatus status) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));
        policy.setStatus(status);
        return toResponse(policyRepository.save(policy));
    }

    public PolicyResponse update(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ApiException("Policy not found", HttpStatus.NOT_FOUND));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ApiException("End date must be after start date", HttpStatus.BAD_REQUEST);
        }

        policy.setPolicyType(request.getPolicyType());
        policy.setPolicyName(request.getPolicyName());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setStartDate(request.getStartDate());
        policy.setEndDate(request.getEndDate());

        return toResponse(policyRepository.save(policy));
    }

    public void delete(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ApiException("Policy not found", HttpStatus.NOT_FOUND);
        }
        policyRepository.deleteById(id);
    }

    private PolicyResponse toResponse(Policy p) {
        return PolicyResponse.builder()
                .id(p.getId())
                .policyNumber(p.getPolicyNumber())
                .customerId(p.getCustomer().getId())
                .customerName(p.getCustomer().getFullName())
                .policyType(p.getPolicyType())
                .policyName(p.getPolicyName())
                .coverageAmount(p.getCoverageAmount())
                .premiumAmount(p.getPremiumAmount())
                .startDate(p.getStartDate())
                .endDate(p.getEndDate())
                .status(p.getStatus())
                .createdOn(p.getCreatedOn())
                .build();
    }
}