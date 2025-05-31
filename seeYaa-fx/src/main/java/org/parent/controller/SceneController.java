package org.parent.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.parent.grpcserviceseeyaa.configuration.validator.GrpcValidatorService;
import org.parent.grpcserviceseeyaa.dto.SignInRequestDto;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.parent.util.AlertWindow;
import org.parent.util.error.AuthorizationValidator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class SceneController {
    @FXML private TextField emailInput;
    @FXML private PasswordField password;
    @FXML private Text noHaveAcc;
    @FXML private Label incorrectInputEmail;
    @FXML private Label incorrectInputPassword;

    private final ConfigurableApplicationContext springContext;
    private final AuthenticationManager authenticationManager;
    private final SecurityService securityService;
    private final GrpcValidatorService grpcValidatorService;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public SceneController(ConfigurableApplicationContext springContext, AuthenticationManager authenticationManager,
                           SecurityService securityService, GrpcValidatorService grpcValidatorService) {
        this.springContext = springContext;
        this.authenticationManager = authenticationManager;
        this.securityService = securityService;
        this.grpcValidatorService = grpcValidatorService;
    }

    @FXML
    public void initialize() {
        noHaveAcc.setOnMouseClicked(event -> {
            try {
                signUp();
            } catch (IOException e) {
                AlertWindow.showAlert(Alert.AlertType.ERROR, "Can't sign up", e.getMessage());
            }
        });
    }

    @FXML
    public void go(ActionEvent event) throws IOException {
        incorrectInputEmail.setVisible(false);
        incorrectInputPassword.setVisible(false);


        try {
            grpcValidatorService.validSignIn(new SignInRequestDto(emailInput.getText(), password.getText()));
            final UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(emailInput.getText(), password.getText());

            final Authentication auth = authenticationManager.authenticate(authToken);
            securityService.setAuthentication(auth);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("email.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/email.css")).toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.show();
        } catch (ConstraintViolationException e) {
            final var check = new AuthorizationValidator(incorrectInputEmail, incorrectInputPassword);
            check.checkFieldsLogin(e);
            incorrectInputPassword = check.getIncorrectInputPassword();
            incorrectInputEmail = check.getIncorrectInputEmail();
        } catch (AuthenticationException e) {
            incorrectInputPassword.setText("Wrong password or email");
            incorrectInputPassword.setVisible(true);
        }
    }

    private void signUp() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signUp.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            stage = new Stage();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects
                    .requireNonNull(this.getClass().getResource("static/signUp.css"))
                    .toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.centerOnScreen();
            stage.setTitle("Sign Up");
            stage.show();
        } catch (Exception e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "Can't sign up", e.getMessage());
        }
    }
}
