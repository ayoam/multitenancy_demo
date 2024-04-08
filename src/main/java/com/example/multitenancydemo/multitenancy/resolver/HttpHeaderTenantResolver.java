package com.example.multitenancydemo.multitenancy.resolver;

import com.example.multitenancydemo.multitenancy.config.TenantHttpProperties;
import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class HttpHeaderTenantResolver implements TenantResolver<HttpServletRequest> {

	private final TenantHttpProperties tenantHttpProperties;

	public HttpHeaderTenantResolver(TenantHttpProperties tenantHttpProperties) {
		this.tenantHttpProperties = tenantHttpProperties;
	}

	@Override
	public String resolveTenantId(@NonNull HttpServletRequest request) {
		return request.getHeader(tenantHttpProperties.headerName());
	}

}
