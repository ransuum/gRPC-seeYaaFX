package org.parent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.practice.seeyaa.configuration.movedletterconf.MovedLetterConfiguration;
import org.practice.seeyaa.enums.TypeOfLetter;
import org.practice.seeyaa.exception.ActionException;
import org.practice.seeyaa.models.dto.LetterDto;
import org.practice.seeyaa.security.SecurityService;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.UsersService;
import org.practice.seeyaa.util.choicesofletters.Choice;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.practice.seeyaa.ui.LetterUIFactory.createTextField;
import static org.practice.seeyaa.util.fieldvalidation.FieldUtil.refractorDate;

@Component
public class EmailController {
    @FXML
    @Getter
    private Text emailOfAuthUser;
    @FXML
    private Button sent;
    @FXML
    private Button deleteButton;
    @FXML
    private Button inboxes;
    @FXML
    private Button spambutton;
    @FXML
    private Button spam;
    @FXML
    private Button garbage;
    @FXML
    private Button write;
    @FXML
    private ImageView searchButton;
    @FXML
    private TextField search;
    @FXML
    @Getter
    private VBox hboxInsideInboxes;
    @FXML
    private ImageView editProfile;

    private static final String SELECTED = "selected";

    private final ConfigurableApplicationContext springContext;
    private final LetterService letterService;
    private final UsersService usersService;
    private final Map<TypeOfLetter, Choice> typeOfLetterChoices;
    private final SecurityService securityService;
    private final MovedLetterConfiguration letterMoveService;
    private final Map<String, Stage> openStages;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public EmailController(ConfigurableApplicationContext springContext, LetterService letterService,
                           UsersService usersService, List<Choice> choices,
                           SecurityService securityService, MovedLetterConfiguration letterMoveService, Map<String, Stage> openStages) {
        this.springContext = springContext;
        this.letterService = letterService;
        this.usersService = usersService;
        this.typeOfLetterChoices = choices.stream()
                .collect(Collectors.toMap(Choice::getChoice, o -> o));
        this.securityService = securityService;
        this.letterMoveService = letterMoveService;
        this.openStages = openStages;
    }

    @FXML
    public void initialize() {
        emailOfAuthUser.setText(securityService.getCurrentUserEmail());
        write.setOnMouseClicked(mouseEvent -> write());
        editProfile.setOnMouseClicked(mouseEvent -> editProfile());

        addToBox(inboxes, 1, TypeOfLetter.INBOXES);
        addToBox(sent, 2, TypeOfLetter.SENT);
        addToBox(spam, 3, TypeOfLetter.SPAM);
        addToBox(garbage, 4, TypeOfLetter.GARBAGE);
        registerSearchHandlers();
    }

    @FXML
    public void delete() {
        processSelectedLetters(TypeOfLetter.GARBAGE);
    }

    @FXML
    public void spam() {
        processSelectedLetters(TypeOfLetter.SPAM);
    }

    @FXML
    public void exit(ActionEvent event) throws IOException {
        final var fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        root = fxmlLoader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/login.css")).toExternalForm());
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
    }

    private void registerSearchHandlers() {
        searchButton.setOnMouseClicked(event -> {
            hboxInsideInboxes.getChildren().clear();

            if (inboxes.getStyleClass().contains(SELECTED))
                letterService.findAllInboxByTopic(search.getText(), emailOfAuthUser.getText())
                        .forEach(letter
                                -> addLetterToUI(letter, 1));
            else if (sent.getStyleClass().contains(SELECTED))
                letterService.findAllSentByTopic(search.getText(), emailOfAuthUser.getText())
                        .forEach(letter
                                -> addLetterToUI(letter, 2));

        });
    }

    private void write() {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource("send.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            SendLetterController controller = fxmlLoader.getController();
            controller.setHiding(emailOfAuthUser.getText());
            stage = new Stage();
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/sendLetter.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Send Letter");
            stage.show();
        } catch (IOException e) {
            throw new ActionException(e);
        }
    }

    private void editProfile() {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            final var byEmail = usersService.findByEmailWithoutLists();

            final EditController controller = fxmlLoader.getController();
            controller.getEmail().setText(byEmail.email());
            controller.getFirstname().setText(byEmail.firstname());
            controller.getLastname().setText(byEmail.lastname());
            controller.getUsername().setText(byEmail.username());
            stage = new Stage();
            scene = new Scene(this.root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/edit.css")).toExternalForm());
            stage.centerOnScreen();
            stage.widthProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> this.stage.centerOnScreen());
            stage.setScene(this.scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            throw new ActionException(e);
        }
    }

    public void addToBox(Button button, int index, TypeOfLetter choice) {
        button.setOnMouseClicked(event -> {
            resetButtonStyles();
            hboxInsideInboxes.getChildren().clear();
            button.getStyleClass().add(SELECTED);

            final var letters = typeOfLetterChoices.get(choice)
                    .addToBox(index, emailOfAuthUser.getText())
                    .stream()
                    .sorted(Comparator.comparing(LetterDto::createdAt).reversed())
                    .toList();

            Optional.of(letters)
                    .filter(list -> !list.isEmpty())
                    .ifPresentOrElse(
                            list -> list.forEach(letter -> addLetterToUI(letter, index)),
                            () -> hboxInsideInboxes.getChildren().add(new Text("No letters found in this category"))
                    );
        });
    }

    public void addLetterToUI(LetterDto letter, int function) {
        final var textField = createTextField(letter, function);
        textField.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/letters.css")).toExternalForm());
        final TextField textField1 = new TextField();
        textField1.setAlignment(Pos.CENTER);
        textField1.setEditable(false);
        textField1.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/date.css")).toExternalForm());
        textField1.setText(refractorDate(letter.createdAt()));

        final CheckBox checkBox = new CheckBox();
        checkBox.setOnAction(event -> {
            textField.setDisable(checkBox.isSelected());
            textField1.setDisable(checkBox.isSelected());
            updateDeleteButtonVisibility();
        });

        final HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(checkBox, textField, textField1);

        textField.setOnMouseClicked(textFieldEvent -> {
            if (!checkBox.isSelected()) handleTextFieldClick(letter.id(), function);
        });

        hBox.setId(letter.id());
        hboxInsideInboxes.getChildren().add(hBox);
    }

    private void updateDeleteButtonVisibility() {
        final boolean anySelected = hboxInsideInboxes.getChildren().stream()
                .filter(HBox.class::isInstance)
                .map(HBox.class::cast)
                .flatMap(hbox -> hbox.getChildren().stream())
                .filter(CheckBox.class::isInstance)
                .anyMatch(child -> ((CheckBox) child).isSelected());

        spambutton.setVisible(anySelected);
        deleteButton.setVisible(anySelected);
    }

    private void resetButtonStyles() {
        inboxes.getStyleClass().remove(SELECTED);
        sent.getStyleClass().remove(SELECTED);
        spam.getStyleClass().remove(SELECTED);
        garbage.getStyleClass().remove(SELECTED);
    }

    private void processSelectedLetters(TypeOfLetter typeOfLetter) {
        final List<HBox> selectedBoxes = new LinkedList<>();
        hboxInsideInboxes.getChildren().forEach(node -> {
            if (node instanceof HBox hBox) {
                CheckBox checkBox = (CheckBox) hBox.getChildren().getFirst();
                if (checkBox.isSelected()) selectedBoxes.add(hBox);
            }
        });

        final var email = emailOfAuthUser.getText();
        selectedBoxes.forEach(hBox -> {
            final String letterId = hBox.getId();
            switch (typeOfLetter) {
                case SPAM -> letterMoveService.setLetterType(letterId, email, TypeOfLetter.SPAM);
                case GARBAGE -> letterMoveService.setLetterType(letterId, email, TypeOfLetter.GARBAGE);
                default -> letterService.deleteById(letterId);
            }
        });

        hboxInsideInboxes.getChildren().removeAll(selectedBoxes);
        spambutton.setVisible(false);
        deleteButton.setVisible(false);
    }

    private void handleTextFieldClick(String letterId, int function) {
        if (openStages.containsKey(letterId)) {
            Stage existingStage = openStages.get(letterId);
            if (existingStage.isShowing()) {
                existingStage.requestFocus();
                return;
            } else openStages.remove(letterId);

        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("checkLetter.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            final var letter1 = letterService.findById(letterId);
            stage = new Stage();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Check Letter");
            final CheckMyLetterController controller = fxmlLoader.getController();
            controller.setLetter(letter1, function);
            controller.setCurrentEmail(emailOfAuthUser.getText());
            stage.widthProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.heightProperty().addListener((obs, oldVal, newVal) -> stage.centerOnScreen());
            stage.centerOnScreen();
            stage.setOnCloseRequest(event -> openStages.remove(letterId));
            stage.initModality(Modality.APPLICATION_MODAL);
            openStages.put(letterId, stage);
            stage.show();
        } catch (IOException e) {
            throw new ActionException(e);
        }
    }
}
