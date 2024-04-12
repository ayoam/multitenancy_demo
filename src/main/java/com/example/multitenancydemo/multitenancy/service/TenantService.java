package com.example.multitenancydemo.multitenancy.service;

import com.example.multitenancydemo.multitenancy.context.TenantContext;
import com.example.multitenancydemo.multitenancy.data.flyway.TenantFlywayMigrationInitializer;
import com.example.multitenancydemo.multitenancy.model.Tenant;
import com.example.multitenancydemo.multitenancy.repository.TenantRepository;
import com.example.multitenancydemo.multitenancy.wrapper.KeycloakWrapper;
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
    private final KeycloakWrapper keycloakWrapper;

    @Transactional
    public Tenant createNewTenant(String schemaName) {
        Tenant tenant = new Tenant();
        tenant.setSchemaName(schemaName);

        //check if schema already exist
        if(isTenantExist(schemaName)) {
            throw new RuntimeException("Schema Name already used");
        }
        // Create tenant realm
        keycloakWrapper.createRealm(schemaName);

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
