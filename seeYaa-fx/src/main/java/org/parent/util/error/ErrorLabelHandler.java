package org.parent.util.error;

public interface ErrorLabelHandler {
    void resetLabels();
    void showEmailError(String message);
    void showPasswordError(String message);
    void showAppError();
}
