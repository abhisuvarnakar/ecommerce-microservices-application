package com.cs.ecommerce.productservice.aspect;

import com.cs.ecommerce.productservice.annotation.RequireRole;
import com.cs.ecommerce.sharedmodules.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
public class RoleCheckAspect {

    @Before("@annotation(requireRole)")
    public void checkRole(RequireRole requireRole) throws AccessDeniedException {
        HttpServletRequest request = ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        String rolesHeader = request.getHeader("X-User-Roles");
        Role requiredRole = requireRole.value();

        if (rolesHeader == null || rolesHeader.isBlank())  {
            throw new AccessDeniedException("Missing roles header for role check");
        }

        List<String> userRoles = Arrays.stream(rolesHeader.split(","))
                .map(String::trim).toList();

        boolean hasRole = userRoles.stream()
                .anyMatch(role -> role.equalsIgnoreCase(requiredRole.name()));

        if (!hasRole) {
            throw new AccessDeniedException("Access denied. Required role: " + requiredRole);
        }
    }
}
