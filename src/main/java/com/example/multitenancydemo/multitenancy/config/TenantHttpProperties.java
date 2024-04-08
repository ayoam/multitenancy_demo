package com.example.multitenancydemo.multitenancy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix = "multitenancy.http")
public record TenantHttpProperties(
		@DefaultValue("X-TenantId")
		String headerName
){}
