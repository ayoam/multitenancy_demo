package com.example.multitenancydemo.multitenancy.repository;

import com.example.multitenancydemo.multitenancy.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    Boolean existsBySchemaNameIgnoreCase(String schemaName);
    Boolean findBySchemaNameIgnoreCase(String schemaName);
}
