package com.example.multitenancydemo.multitenancy.config;

import com.example.multitenancydemo.multitenancy.web.TenantContextFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

@Configuration(proxyBeanMethods = false)
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
            TenantContextFilter tenantContextFilter
    ) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(authenticationManagerResolver))
                .addFilterBefore(tenantContextFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }
}
