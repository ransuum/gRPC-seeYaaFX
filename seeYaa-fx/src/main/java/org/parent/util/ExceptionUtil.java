package org.parent.util;

import io.grpc.StatusRuntimeException;

public class ExceptionUtil {

    private ExceptionUtil() { }

    public static String getMessageFromError(Throwable error) {
        return switch (error) {
            case StatusRuntimeException st -> st.getStatus().getDescription();
            case NullPointerException npe -> "A required value was missing: " + npe.getMessage();
            case IllegalArgumentException iae -> "Invalid argument provided: " + iae.getMessage();
            default -> "An unexpected error occurred: " + error.getMessage();
        };
    }
}
