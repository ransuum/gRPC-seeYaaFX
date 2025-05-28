package org.parent.ui;

import com.seeYaa.proto.email.Letter;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.parent.grpcserviceseeyaa.util.fieldvalidation.FieldUtil.refractorField;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LetterUIFactory {

    public static TextField createTextField(Letter letter, int function) {
        final var textField = new TextField();
        textField.setCursor(Cursor.HAND);
        textField.setPrefWidth(600);
        textField.setId(letter.getId());

        final var byName = (function == 1)
                ? refractorField(25, "By:", letter.getUserBy().getFirstname(), letter.getUserBy().getLastname())
                : refractorField(25, "To:", letter.getUserTo().getFirstname(), letter.getUserTo().getUsername());

        final var paddedTopic = refractorField(30, letter.getTopic());

        textField.setText(byName + paddedTopic);
        textField.setEditable(false);
        return textField;
    }
}
