package com.example.multitenancydemo.customer.repository;

import java.util.List;
import java.util.UUID;

import com.example.multitenancydemo.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,UUID> {
	List<Customer> findByType(String type);
}
