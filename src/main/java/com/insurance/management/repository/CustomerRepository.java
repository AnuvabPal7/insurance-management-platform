package com.insurance.management.repository;

import com.insurance.management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(Long userId);
    Optional<Customer> findByUserEmail(String email);
    boolean existsByPhone(String phone);
}