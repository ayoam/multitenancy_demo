package com.example.multitenancydemo.multitenancy.web;

import com.example.multitenancydemo.multitenancy.context.TenantContext;
import com.example.multitenancydemo.multitenancy.resolver.HttpHeaderTenantResolver;
import com.example.multitenancydemo.multitenancy.service.TenantService;
import io.micrometer.common.KeyValue;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ServerHttpObservationFilter;

import java.io.IOException;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    private final HttpHeaderTenantResolver httpRequestTenantResolver;
    private final TenantService tenantService;

    public TenantContextFilter(HttpHeaderTenantResolver httpHeaderTenantResolver, TenantService tenantService) {
        this.httpRequestTenantResolver = httpHeaderTenantResolver;
        this.tenantService = tenantService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tenantIdentifier = httpRequestTenantResolver.resolveTenantId(request);
        System.out.println(tenantIdentifier);
        // Disable tenant verification on tenant creation
        if (!(request.getMethod().equals(HttpMethod.POST.toString()) && request.getRequestURI().contains("/tenants"))) {
            if (StringUtils.hasText(tenantIdentifier) && isTenantValid(tenantIdentifier)) {
                TenantContext.setTenantId(tenantIdentifier);
                configureLogs(tenantIdentifier);
                configureTraces(tenantIdentifier, request);
            } else {
                throw new RuntimeException("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));
//            throw new TenantResolutionException("A valid tenant must be specified for requests to %s".formatted(request.getRequestURI()));
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            clear();
        }
    }

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return request.getRequestURI().startsWith("/actuator");
//    }

    private boolean isTenantValid(String tenantIdentifier) {
        return tenantService.isTenantExist(tenantIdentifier);
    }

    private void configureLogs(String tenantId) {
        MDC.put("tenantId", tenantId);
    }

    private void configureTraces(String tenantId, HttpServletRequest request) {
        ServerHttpObservationFilter.findObservationContext(request).ifPresent(context ->
                context.addHighCardinalityKeyValue(KeyValue.of("tenant.id", tenantId)));
    }

    private void clear() {
        MDC.remove("tenantId");
        TenantContext.clear();
    }

}
