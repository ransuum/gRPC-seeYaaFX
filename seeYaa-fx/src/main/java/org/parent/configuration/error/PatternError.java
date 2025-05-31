package org.parent.configuration.error;

import lombok.Getter;

@Getter
public enum PatternError {
    EMAIL_PATTERN("findByEmailForPassword.signInRequest.email: "),
    PASSWORD_PATTERN("findByEmailForPassword.signInRequest.password: ");

    private final String value;

    PatternError(String value) {
        this.value = value;
    }
}
