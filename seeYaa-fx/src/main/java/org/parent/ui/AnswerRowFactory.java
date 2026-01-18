package org.parent.ui;

import com.seeYaa.proto.email.Answer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.parent.util.DateConfigurer;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerRowFactory {

    public static HBox createAnswerRow(Answer answerDto, Consumer<String> textSetter) {
        var answerRow = new HBox();
        answerRow.getStyleClass().add("answer-row");
        answerRow.setSpacing(15);
        answerRow.setAlignment(Pos.CENTER_LEFT);

        var avatar = FileUiUtils.createAvatar(answerDto.getUserByAnswered().getFirstname());

        var contentBox = new VBox(2);
        contentBox.getStyleClass().add("answer-content");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        var headerBox = new HBox(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        var nameLabel = new Label(answerDto.getUserByAnswered().getFirstname());
        nameLabel.getStyleClass().add("answer-name");

        var dateLabel = new Label(DateConfigurer.getDate(answerDto.getCreatedAt()));
        dateLabel.getStyleClass().add("answer-date");

        headerBox.getChildren().addAll(nameLabel, dateLabel);

        String previewText = answerDto.getAnswerText().replace("\n", " ");
        if (previewText.length() > 30) previewText = previewText.substring(0, 30) + "...";
        Label previewLabel = new Label(previewText);
        previewLabel.getStyleClass().add("answer-preview");

        contentBox.getChildren().addAll(headerBox, previewLabel);

        answerRow.getChildren().addAll(avatar, contentBox);

        answerRow.setOnMouseClicked(_ -> textSetter.accept(answerDto.getAnswerText()));

        return answerRow;
    }
}
