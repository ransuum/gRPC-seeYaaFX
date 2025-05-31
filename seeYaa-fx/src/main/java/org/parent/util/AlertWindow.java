package org.parent.util;

import javafx.scene.control.Alert;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlertWindow {

    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        final Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
