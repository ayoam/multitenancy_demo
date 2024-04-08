package com.example.multitenancydemo.multitenancy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "multitenancy.data")
public record TenantDataProperties(
		@DefaultValue("default")
		String defaultSchema
){}
