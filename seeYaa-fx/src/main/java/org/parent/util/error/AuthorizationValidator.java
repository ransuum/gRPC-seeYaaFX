package org.parent.util.error;

import jakarta.validation.ConstraintViolationException;
import javafx.scene.control.Label;
import org.parent.configuration.error.PatternError;

public final class AuthorizationValidator extends DefaultErrorLabelHandler implements Check {

    public AuthorizationValidator(Label incorrectInputEmail, Label incorrectInputPassword) {
        super(incorrectInputEmail, incorrectInputPassword);
    }

    @Override
    public void checkFieldsRegistration() {
        // Noncompliant - method is empty
    }

    @Override
    public void checkFieldsLogin(ConstraintViolationException e) {
        var errorMessage = e.getLocalizedMessage();
        final boolean[] errors = new boolean[]{
                errorMessage.contains(PatternError.EMAIL_PATTERN.getValue()),
                errorMessage.contains(PatternError.PASSWORD_PATTERN.getValue())
        };

        resetLabels();

        if (errors[0] && errors[1])
            showEmailError("Email and password is blank or not correct for input");
        else if (errors[1])
            showPasswordError("One upper case, 9-36 length, special symbol, numbers");
        else if (errors[0])
            showEmailError(errorMessage.replace(PatternError.EMAIL_PATTERN.getValue(), ""));
        else showAppError();

    }
}
