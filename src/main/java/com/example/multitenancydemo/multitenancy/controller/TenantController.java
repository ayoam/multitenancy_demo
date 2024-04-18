package com.example.multitenancydemo.multitenancy.controller;

import com.example.multitenancydemo.multitenancy.dto.CreateTenantDto;
import com.example.multitenancydemo.multitenancy.model.Tenant;
import com.example.multitenancydemo.multitenancy.service.TenantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
@AllArgsConstructor
public class TenantController {
    private TenantService tenantService;
    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody CreateTenantDto dto) {
        return new ResponseEntity<>(tenantService.createNewTenant(dto.getTenantId()),HttpStatus.CREATED);
    }
}
