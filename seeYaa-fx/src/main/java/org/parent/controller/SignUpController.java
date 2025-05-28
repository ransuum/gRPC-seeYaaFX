package org.parent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.practice.seeyaa.models.request.SignUpRequestDto;
import org.practice.seeyaa.service.UsersService;
import org.springframework.stereotype.Component;

@Component
public class SignUpController {
    @FXML private TextField email;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField password;
    @FXML private TextField username;

    private final UsersService usersService;

    public SignUpController(UsersService usersService) {
        this.usersService = usersService;
    }

    @FXML
    public void signUp(ActionEvent event) {
        registry();
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void registry(){
        usersService.save(new SignUpRequestDto(
                email.getText(), username.getText(), password.getText(),
                firstName.getText(), lastName.getText())
        );
    }
}
