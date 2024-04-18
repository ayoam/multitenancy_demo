package com.example.multitenancydemo.multitenancy.service;

import com.example.multitenancydemo.multitenancy.context.TenantContext;
import com.example.multitenancydemo.multitenancy.data.flyway.TenantFlywayMigrationInitializer;
import com.example.multitenancydemo.multitenancy.model.Tenant;
import com.example.multitenancydemo.multitenancy.repository.TenantRepository;
import com.example.multitenancydemo.multitenancy.wrapper.KeycloakWrapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Tenant createNewTenant(String tenantId) {
        Tenant tenant = new Tenant(tenantId);

        //check if schema already exist
        if(isTenantExist(tenantId)) {
            throw new RuntimeException("TenantId already used");
        }

        // Create tenant realm and default user
        keycloakWrapper.createRealm(tenantId);
        keycloakWrapper.createUser(tenantId,"default-user","default-user", "ROLE_ADMIN");

        //build schema
        flywayBuilder.buildDatabaseSchema(tenantId);

        //save tenant
        return tenantRepository.save(tenant);
    }

    public boolean isTenantExist(String identifier) {
        return tenantRepository.existsByTenantIdIgnoreCase(identifier);
    }
    private void buildDatabaseSchema(String schema) {
        flywayBuilder.tenantFlyway(schema).migrate();
    }
}
