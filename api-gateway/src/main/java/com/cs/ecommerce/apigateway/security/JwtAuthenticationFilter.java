package com.cs.ecommerce.apigateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String[] PUBLIC_PATHS = {
            "/users/auth",
            "/actuator",
            "/swagger",
            "/v3/api-docs"
    };
    private static final String[] PUBLIC_PATTERNS = {
            "^/actuator.*",
            "^/[^/]+/swagger.*",
            "^/[^/]+/v3/api-docs.*"
    };
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        log.info("Gateway Request Path: {}", path);

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtService.getAllClaimsFromToken(token);

            String roles = "";
            Object rolesClaim = claims.get("roles");
            if (rolesClaim instanceof List) {
                roles = String.join(",", ((List<?>) rolesClaim).stream()
                        .map(Object::toString)
                        .toList());
            }

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(claims.getSubject()))
                    .header("X-User-Email", claims.get("email", String.class))
                    .header("X-User-Roles", roles)
                    .build();

            log.info("X-User-Id: {} | X-User-Email: {} | X-User-Roles: {}",
                    claims.getSubject(), claims.get("email", String.class), roles);
            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            log.error("JWT Authentication failed for path: {}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC_PATHS).anyMatch(path::startsWith)
                || Arrays.stream(PUBLIC_PATTERNS).anyMatch(path::matches);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
