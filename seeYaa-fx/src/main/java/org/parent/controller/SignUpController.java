package org.parent.controller;

import com.seeYaa.proto.email.service.users.SignUpRequest;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.parent.grpcserviceseeyaa.configuration.validator.GrpcValidatorService;
import org.parent.grpcserviceseeyaa.mapper.UserMapper;
import org.parent.util.AlertWindow;
import org.springframework.stereotype.Component;

@Component
public class SignUpController {

    @FXML private TextField email;
    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField password;
    @FXML private TextField username;

    private final UsersServiceGrpc.UsersServiceBlockingStub usersService;
    private final GrpcValidatorService grpcValidatorService;

    public SignUpController(UsersServiceGrpc.UsersServiceBlockingStub usersService,
                            GrpcValidatorService grpcValidatorService) {
        this.usersService = usersService;
        this.grpcValidatorService = grpcValidatorService;
    }

    @FXML
    public void signUp(ActionEvent event) {
        registry();
        var stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void registry() {
        try {
            var signUpReq = SignUpRequest.newBuilder()
                    .setEmail(email.getText())
                    .setUsername(username.getText())
                    .setPassword(password.getText())
                    .setFirstname(firstName.getText())
                    .setLastname(lastName.getText())
                    .build();
            grpcValidatorService.validSignUp(UserMapper.INSTANCE.toSignUpRequestDto(signUpReq));
            usersService.signUp(signUpReq);
        } catch (Exception e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "SignUp Error", e.getMessage());
        }
    }
}
