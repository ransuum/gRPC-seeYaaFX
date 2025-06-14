package org.parent.grpcserviceseeyaa.security.rolechecker;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final SecurityService securityService;

    @Around("@annotation(authorize)")
    public Object around(ProceedingJoinPoint pjp, Authorize authorize) throws Throwable {

        String value = authorize.value();
        if (value.startsWith("hasRole")) {
            String role = value.substring(value.indexOf('\'') + 1,
                    value.lastIndexOf('\''));
            if (!securityService.getCurrentUserRoles().contains(role)) {
                throw new StatusRuntimeException(Status.PERMISSION_DENIED
                        .withDescription("You do not have permission to access this resource"));
            }
        }

        return pjp.proceed();
    }
}
