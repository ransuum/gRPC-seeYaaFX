package org.parent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.parent.util.AlertWindow;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private Stage stage;
    private Scene scene;
    private Parent root;

    public SceneController(ConfigurableApplicationContext springContext, AuthenticationManager authenticationManager,
                           SecurityService securityService) {
        this.springContext = springContext;
        this.authenticationManager = authenticationManager;
        this.securityService = securityService;
    }

    @FXML
    public void initialize() {
        noHaveAcc.setOnMouseClicked(event -> {
            try {
                signUp();
            } catch (IOException e) {
                AlertWindow.showAlert("Can't sign up", e.getMessage());
            }
        });
    }

    @FXML
    public void go(ActionEvent event) throws IOException {
        incorrectInputEmail.setVisible(false);
        incorrectInputPassword.setVisible(false);
        final UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(emailInput.getText(), password.getText());

        try {
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
        } catch (Exception e) {
            AlertWindow.showAlert("Can't sign up", e.getMessage());
        }

    }

    private void signUp() throws IOException {
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
    }
}
