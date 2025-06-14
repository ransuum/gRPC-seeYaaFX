package org.parent.ui;

import com.seeYaa.proto.email.Answer;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.parent.util.DateConfigurer;

import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerRowFactory {

    public static HBox createAnswerRow(Answer answerDto, Consumer<String> textSetter) {
        final var answerRow = new HBox();
        answerRow.getStyleClass().add("answer-row");
        answerRow.setSpacing(10);
        answerRow.setAlignment(Pos.CENTER_LEFT);

        final var avatar = FileUiUtils.createAvatar(answerDto.getUserByAnswered().getFirstname());

        final var contentBox = new VBox(5);
        contentBox.getStyleClass().add("answer-content");
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        final var nameLabel = new Label(answerDto.getUserByAnswered().getFirstname());
        nameLabel.getStyleClass().add("answer-name");

        final var dateLabel = new Label(DateConfigurer.getDate(answerDto.getCreatedAt()));
        dateLabel.getStyleClass().add("answer-date");

        final var headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(nameLabel, dateLabel);

        contentBox.getChildren().addAll(headerBox);

        answerRow.getChildren().addAll(avatar, contentBox);

        answerRow.setOnMouseClicked(event -> {
            textSetter.accept(answerDto.getAnswerText());
            var textFade = new FadeTransition(Duration.millis(300), answerRow);
            textFade.setFromValue(0.5);
            textFade.setToValue(1);
            textFade.play();
        });

        return answerRow;
    }
}
