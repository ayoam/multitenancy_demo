package com.example.multitenancydemo.multitenancy.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.multitenancydemo.multitenancy.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;

import com.example.multitenancydemo.multitenancy.context.TenantContext;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
public class TenantAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private static final Map<String,AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();
    private final TenantService tenantService;

    public TenantAuthenticationManagerResolver(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        var tenantId = TenantContext.getTenantId();
        return authenticationManagers.computeIfAbsent(tenantId, this::buildAuthenticationManager);
    }

    private AuthenticationManager buildAuthenticationManager(String tenantId) {
        var issuerUri = "http://localhost:8080/realms/" + tenantId;
        var jwtAuthenticationprovider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(issuerUri));
        return jwtAuthenticationprovider::authenticate;
    }

}
