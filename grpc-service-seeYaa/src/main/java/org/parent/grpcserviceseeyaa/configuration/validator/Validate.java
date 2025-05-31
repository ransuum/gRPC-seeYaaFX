package org.parent.grpcserviceseeyaa.configuration.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Set;
import java.util.stream.Collectors;

public record Validate<T>(Set<ConstraintViolation<T>> violations) {
    public void validateAndMaybeThrow() {
        String errorMsg = violations.stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining("; "));
        throw new ConstraintViolationException(errorMsg, violations);
    }
}
