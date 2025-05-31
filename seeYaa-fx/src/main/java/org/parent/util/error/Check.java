package org.parent.util.error;

import jakarta.validation.ConstraintViolationException;

public sealed interface Check permits AuthorizationValidator {
    void checkFieldsRegistration();
    void checkFieldsLogin(ConstraintViolationException e);
}
