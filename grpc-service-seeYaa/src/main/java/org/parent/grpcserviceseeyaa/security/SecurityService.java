package org.parent.grpcserviceseeyaa.security;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.security.contextholder.AuthenticationStore;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationStore authenticationStore;

    public String getCurrentUserEmail() {
        final var auth = authenticationStore.get();
        if (auth == null)
            throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("You are not logged in"));
        return auth.email();
    }

    public Set<String> getCurrentUserRoles() {
        final var auth = authenticationStore.get();
        if (auth == null)
            throw new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("You are not logged in"));
        return auth.roles();
    }
}
