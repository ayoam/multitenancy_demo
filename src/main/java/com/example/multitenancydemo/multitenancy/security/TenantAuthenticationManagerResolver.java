package com.example.multitenancydemo.multitenancy.security;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.example.multitenancydemo.multitenancy.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;

import com.example.multitenancydemo.multitenancy.context.TenantContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    private static final Map<String,AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();
    private final TenantService tenantService;

    public TenantAuthenticationManagerResolver(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        // Use default realm if there is no tenant in TenantContext
        var tenantId = Objects.requireNonNullElse(TenantContext.getTenantId(), "default-realm");
        return authenticationManagers.computeIfAbsent(tenantId, this::buildAuthenticationManager);
    }

    private AuthenticationManager buildAuthenticationManager(String tenantId) {
        var issuerUri = serverUrl + "/realms/" + tenantId;
        var jwtAuthenticationprovider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuerUri));
        return jwtAuthenticationprovider::authenticate;
    }

}
