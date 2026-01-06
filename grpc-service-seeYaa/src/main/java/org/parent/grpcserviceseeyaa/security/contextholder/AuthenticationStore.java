package org.parent.grpcserviceseeyaa.security.contextholder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("singleton")
public class AuthenticationStore {

    private AuthenticationObject cached;

    public void set(AuthenticationObject auth) {
        cached = auth;
    }

    public Optional<AuthenticationObject> get() {
        return Optional.ofNullable(this.cached);
    }
}
