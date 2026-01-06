package org.parent.grpcserviceseeyaa.security;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.parent.grpcserviceseeyaa.security.contextholder.AuthenticationObject;
import org.parent.grpcserviceseeyaa.security.contextholder.AuthenticationStore;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationStore authenticationStore;

    public String getCurrentUserEmail() {
        return authenticationStore.get().map(AuthenticationObject::email)
                .orElseThrow(() -> new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("You are not logged in")));
    }

    public Set<String> getCurrentUserRoles() {
        return authenticationStore.get().map(AuthenticationObject::roles)
                .orElseThrow(() -> new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("You are not logged in")));
    }
}
