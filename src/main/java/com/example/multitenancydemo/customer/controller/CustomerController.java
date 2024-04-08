package com.example.multitenancydemo.customer.controller;

import java.util.List;

import com.example.multitenancydemo.customer.model.Customer;
import com.example.multitenancydemo.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
public class CustomerController {

	private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
	private final CustomerRepository customerRepository;

	CustomerController(CustomerRepository customerRepository) {
    	this.customerRepository = customerRepository;
	}

  	@GetMapping
  	List<Customer> getCustomers() {
    	return customerRepository.findAll();
  	}

	@PostMapping
	Customer addCustomer(@RequestBody Customer customer) {
    	return customerRepository.save(customer);
	}

}
