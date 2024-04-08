package com.example.multitenancydemo.multitenancy.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TenantHttpProperties.class, TenantDataProperties.class})
public class TenantAppConfig {

}
