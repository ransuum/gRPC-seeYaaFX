package org.parent.util.error;

import javafx.scene.control.Label;
import lombok.Getter;

@Getter
public class DefaultErrorLabelHandler implements ErrorLabelHandler {
    protected Label incorrectInputEmail;
    protected Label incorrectInputPassword;

    public DefaultErrorLabelHandler(Label incorrectInputEmail, Label incorrectInputPassword) {
        this.incorrectInputEmail = incorrectInputEmail;
        this.incorrectInputPassword = incorrectInputPassword;
    }

    @Override
    public void resetLabels() {
        if (incorrectInputEmail == null) incorrectInputEmail = new Label();
        if (incorrectInputPassword == null) incorrectInputPassword = new Label();

        incorrectInputEmail.setVisible(false);
        incorrectInputPassword.setVisible(false);
    }

    @Override
    public void showEmailError(String message) {
        incorrectInputEmail.setText(message);
        incorrectInputEmail.setVisible(true);
    }

    @Override
    public void showPasswordError(String message) {
        incorrectInputPassword.setText(message);
        incorrectInputPassword.setVisible(true);
    }

    @Override
    public void showAppError() {
        incorrectInputPassword.setText("APPLICATION ERROR");
        incorrectInputEmail.setVisible(true);
    }
}
