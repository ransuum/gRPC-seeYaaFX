package org.parent.grpcserviceseeyaa.security.rolechecker;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final SecurityService securityService;

    @Around("@annotation(authorize)")
    public Object around(ProceedingJoinPoint pjp, Authorize authorize) throws Throwable {
        String value = authorize.value();

        try {
            if (value.startsWith("hasRole")) {
                String role = value.substring(value.indexOf('\'') + 1, value.lastIndexOf('\''));
                var userRoles = securityService.getCurrentUserRoles();

                if (StringUtils.isBlank(role)) {
                    log.warn("Access denied - no roles found for user");
                    throw new StatusRuntimeException(Status.UNAUTHENTICATED
                            .withDescription("Authentication required"));
                }

                if (!userRoles.contains(role)) {
                    log.warn("Access denied - required role '{}' not found for user with roles: {}", role, userRoles);
                    throw new StatusRuntimeException(Status.PERMISSION_DENIED
                            .withDescription("Insufficient permissions"));
                }
            }
        } catch (StatusRuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authorization check failed", e);
            throw new StatusRuntimeException(Status.INTERNAL
                    .withDescription("Authorization check failed"));
        }

        return pjp.proceed();
    }
}
