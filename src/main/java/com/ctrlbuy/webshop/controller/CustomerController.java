package com.ctrlbuy.webshop.controller;

import com.ctrlbuy.webshop.exception.CustomerNotFoundException;
import com.ctrlbuy.webshop.model.CustomerEntity;
import com.ctrlbuy.webshop.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customer/{id}")
    public CustomerEntity getCustomer(@PathVariable Long id) {
        return customerService.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Kunden med ID %d hittades inte", id)));
    }
}