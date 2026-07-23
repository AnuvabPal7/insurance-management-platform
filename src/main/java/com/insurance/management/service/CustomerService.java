package com.insurance.management.service;

import com.insurance.management.dto.*;
import com.insurance.management.entity.Customer;
import com.insurance.management.entity.User;
import com.insurance.management.exception.ApiException;
import com.insurance.management.repository.CustomerRepository;
import com.insurance.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerResponse createProfile(String userEmail, CustomerRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (customerRepository.findByUserId(user.getId()).isPresent()) {
            throw new ApiException("Customer profile already exists", HttpStatus.CONFLICT);
        }

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new ApiException("Phone number already registered", HttpStatus.CONFLICT);
        }

        Customer customer = Customer.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public CustomerResponse getMyProfile(String userEmail) {
        Customer customer = customerRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new ApiException("Customer profile not found", HttpStatus.NOT_FOUND));
        return toResponse(customer);
    }

    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
        return toResponse(customer);
    }

    public List<CustomerResponse> getAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPincode(request.getPincode());

        Customer updated = customerRepository.save(customer);
        return toResponse(updated);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ApiException("Customer not found", HttpStatus.NOT_FOUND);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .fullName(c.getFullName())
                .email(c.getUser().getEmail())
                .phone(c.getPhone())
                .dateOfBirth(c.getDateOfBirth())
                .address(c.getAddress())
                .city(c.getCity())
                .state(c.getState())
                .pincode(c.getPincode())
                .registeredOn(c.getRegisteredOn())
                .build();
    }
}