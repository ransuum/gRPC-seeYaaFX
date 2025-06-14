package org.parent.grpcserviceseeyaa.security.contextholder;

import java.util.Objects;
import java.util.Set;

public record AuthenticationObject(String email, Set<String> roles) {
    public AuthenticationObject {
        roles = Set.copyOf(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationObject that = (AuthenticationObject) o;
        return Objects.equals(email(), that.email()) && Objects.equals(roles(), that.roles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email(), roles());
    }
}
