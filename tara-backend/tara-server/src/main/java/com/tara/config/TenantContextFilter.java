package com.tara.config;

import com.tara.common.context.TenantContext;
import com.tara.common.context.TenantContextHolder;
import com.tara.common.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TenantContextFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public TenantContextFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            UUID orgId = parseUuid(request.getHeader("X-Tenant-ID"));
            UUID workspaceId = parseUuid(request.getHeader("X-Workspace-ID"));
            UUID userId = null;
            String email = "anonymous@tara.ai";
            String role = "ANONYMOUS";

            String authHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    Claims claims = jwtTokenProvider.getClaims(token);
                    userId = UUID.fromString(claims.getSubject());
                    email = claims.get("email", String.class);
                    role = claims.get("role", String.class);

                    String tokenOrgId = claims.get("orgId", String.class);
                    if (orgId == null && StringUtils.hasText(tokenOrgId)) {
                        orgId = UUID.fromString(tokenOrgId);
                    }

                    String tokenWorkspaceId = claims.get("workspaceId", String.class);
                    if (workspaceId == null && StringUtils.hasText(tokenWorkspaceId)) {
                        workspaceId = UUID.fromString(tokenWorkspaceId);
                    }
                }
            }

            TenantContextHolder.setContext(TenantContext.of(orgId, workspaceId, userId, email, role));
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
