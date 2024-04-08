package com.example.multitenancydemo.multitenancy.resolver;

import org.springframework.lang.NonNull;

@FunctionalInterface
public interface TenantResolver<T> {

	String resolveTenantId(@NonNull T object);

}
