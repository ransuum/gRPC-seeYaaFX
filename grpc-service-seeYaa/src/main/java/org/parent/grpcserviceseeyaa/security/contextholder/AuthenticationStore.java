package org.parent.grpcserviceseeyaa.security.contextholder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class AuthenticationStore {

    private AuthenticationObject cached;

    public void set(AuthenticationObject auth) {
        cached = auth;
    }

    public AuthenticationObject get() {
        return this.cached;
    }
}
