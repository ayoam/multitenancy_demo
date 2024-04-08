package com.example.multitenancydemo.multitenancy.data.hibernate;

import java.util.Map;
import java.util.Objects;

import com.example.multitenancydemo.multitenancy.config.TenantDataProperties;
import com.example.multitenancydemo.multitenancy.context.TenantContext;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

	private final TenantDataProperties tenantDataProperties;

	public TenantIdentifierResolver(TenantDataProperties tenantDataProperties) {
		this.tenantDataProperties = tenantDataProperties;
	}

	@Override
	public String resolveCurrentTenantIdentifier() {
		return Objects.requireNonNullElse(TenantContext.getTenantId(), tenantDataProperties.defaultSchema());
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
	}

}
