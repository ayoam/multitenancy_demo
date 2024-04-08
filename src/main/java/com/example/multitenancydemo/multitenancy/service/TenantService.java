package com.example.multitenancydemo.multitenancy.service;

import com.example.multitenancydemo.multitenancy.context.TenantContext;
import com.example.multitenancydemo.multitenancy.data.flyway.TenantFlywayMigrationInitializer;
import com.example.multitenancydemo.multitenancy.model.Tenant;
import com.example.multitenancydemo.multitenancy.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public
class TenantService {
    private final TenantFlywayMigrationInitializer flywayBuilder;
    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant createNewTenant(String schemaName) {
        Tenant tenant = new Tenant();
        tenant.setSchemaName(schemaName);

        //check if schema already exist
        if(isTenantExist(schemaName)) {
            throw new RuntimeException("Schema Name already used");
        }
        //build schema
        flywayBuilder.buildDatabaseSchema(schemaName);

        //save tenant
        return tenantRepository.save(tenant);
    }

    public boolean isTenantExist(String identifier) {
        return tenantRepository.existsBySchemaNameIgnoreCase(identifier);
    }
    private void buildDatabaseSchema(String schema) {
        flywayBuilder.tenantFlyway(schema).migrate();
    }
}
