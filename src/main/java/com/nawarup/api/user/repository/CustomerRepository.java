package com.nawarup.api.user.repository;

import com.nawarup.api.user.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
