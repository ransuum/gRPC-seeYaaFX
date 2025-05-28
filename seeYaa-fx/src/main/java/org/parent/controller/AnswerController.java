package org.parent.controller;

import com.seeYaa.proto.email.service.answer.AnswerServiceGrpc;
import com.seeYaa.proto.email.service.answer.CreateAnswerRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
public class AnswerController {
    @FXML private Label idOfLetter;
    @FXML private TextArea textOfAnswer;

    private String emailBy;

    private final AnswerServiceGrpc.AnswerServiceBlockingStub answerService;

    public AnswerController(AnswerServiceGrpc.AnswerServiceBlockingStub answerService) {
        this.answerService = answerService;
    }

    @FXML
    void cancel(ActionEvent event) {
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setIdOfLetter(String idOfLetter) {
        this.idOfLetter.setText(idOfLetter);
    }

    @FXML
    void answer(ActionEvent event) {
        setInformation();
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void setInformation(){
        answerService.createAnswer(CreateAnswerRequest.newBuilder()
                        .setText(textOfAnswer.getText())
                        .setEmailBy(emailBy)
                        .setLetterId(idOfLetter.getText())
                .build());
    }
}
