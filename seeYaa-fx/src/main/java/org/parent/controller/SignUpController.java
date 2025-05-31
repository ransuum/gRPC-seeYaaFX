package org.parent.controller;

import com.seeYaa.proto.email.service.users.SignUpRequest;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.parent.util.AlertWindow;
import org.springframework.stereotype.Component;

@Component
public class SignUpController {
    @FXML
    private TextField email;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField password;
    @FXML
    private TextField username;

    private final UsersServiceGrpc.UsersServiceBlockingStub usersService;

    public SignUpController(UsersServiceGrpc.UsersServiceBlockingStub usersService) {
        this.usersService = usersService;
    }

    @FXML
    public void signUp(ActionEvent event) {
        registry();
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void registry() {
        try {
            usersService.signUp(SignUpRequest.newBuilder()
                    .setEmail(email.getText())
                    .setUsername(username.getText())
                    .setPassword(password.getText())
                    .setFirstname(firstName.getText())
                    .setLastname(lastName.getText())
                    .build());
        } catch (StatusRuntimeException e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "SignUp Error", e.getMessage());
        }
    }
}
