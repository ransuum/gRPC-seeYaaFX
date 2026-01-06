package org.parent.controller;

import com.google.protobuf.Empty;
import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.TypeOfLetter;
import com.seeYaa.proto.email.configuration.movedletter.MovedLetterConfigurationGrpc;
import com.seeYaa.proto.email.configuration.movedletter.SetLetterTypeRequest;
import com.seeYaa.proto.email.service.letter.*;
import com.seeYaa.proto.email.service.users.UsersServiceGrpc;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.parent.grpcserviceseeyaa.util.fieldvalidation.FieldUtil;
import org.parent.ui.LetterUIFactory;
import org.parent.util.AlertWindow;
import org.parent.util.choicesofletters.Choice;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import module java.base;

@Slf4j
@Component
public class EmailController {

    @FXML @Getter @Setter private Text emailOfAuthUser;
    @FXML private Button sent;
    @FXML private Button deleteButton;
    @FXML private Button inboxes;
    @FXML private Button spambutton;
    @FXML private Button spam;
    @FXML private Button garbage;
    @FXML private Button write;
    @FXML private ImageView searchButton;
    @FXML private TextField search;
    @FXML @Getter private VBox hboxInsideInboxes;
    @FXML private ImageView editProfile;
    @FXML private Text inboxCount;

    private static final String SELECTED = "selected";

    private final ConfigurableApplicationContext springContext;
    private final LetterServiceGrpc.LetterServiceBlockingStub letterService;
    private final UsersServiceGrpc.UsersServiceBlockingStub usersService;
    private final Map<TypeOfLetter, Choice> typeOfLetterChoices;
    private final MovedLetterConfigurationGrpc.MovedLetterConfigurationBlockingStub letterMoveService;
    private final Map<String, Stage> openStages;
    private final SecurityService securityService;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public EmailController(ConfigurableApplicationContext springContext,
                           LetterServiceGrpc.LetterServiceBlockingStub letterService,
                           UsersServiceGrpc.UsersServiceBlockingStub usersService, List<Choice> choices,
                           MovedLetterConfigurationGrpc.MovedLetterConfigurationBlockingStub letterMoveService,
                           Map<String, Stage> openStages, SecurityService securityService) {
        this.springContext = springContext;
        this.letterService = letterService;
        this.usersService = usersService;
        this.typeOfLetterChoices = choices.stream()
                .collect(Collectors.toMap(Choice::getChoice, o -> o));
        this.letterMoveService = letterMoveService;
        this.openStages = openStages;
        this.securityService = securityService;
    }

    @FXML
    public void initialize() {
        this.emailOfAuthUser.setText(securityService.getCurrentUserEmail());
        write.setOnMouseClicked(_ -> write());
        editProfile.setOnMouseClicked(_ -> editProfile());

        addToBox(inboxes, 1, TypeOfLetter.INBOXES);
        addToBox(sent, 2, TypeOfLetter.SENT);
        addToBox(spam, 3, TypeOfLetter.SPAM);
        addToBox(garbage, 4, TypeOfLetter.GARBAGE);
        registerSearchHandlers();

        inboxCount.setText(String.valueOf(letterService.countOfInboxesLetter(
                LetterByEmail.newBuilder().setByEmail(emailOfAuthUser.getText()).build()).getCount()
        ));

        resetButtonStyles();
        inboxes.getStyleClass().add(SELECTED);
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

            if (inboxes.getStyleClass().contains(SELECTED)) {
                letterService.findAllInboxByTopic(TopicSearchRequestTo.newBuilder()
                                .setTopic(search.getText())
                                .setUserToEmail(emailOfAuthUser.getText())
                                .build()).getLettersList()
                        .forEach(letter
                                -> addLetterToUI(letter, 1));
            } else if (sent.getStyleClass().contains(SELECTED)) {
                letterService.findAllSentByTopic(TopicSearchRequestBy.newBuilder()
                                .setTopic(search.getText())
                                .setUserByEmail(emailOfAuthUser.getText())
                                .build()).getLettersList()
                        .forEach(letter
                                -> addLetterToUI(letter, 2));
            }

        });
    }

    private void write() {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("send.fxml"));
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
            AlertWindow.showAlert(Alert.AlertType.ERROR, "Writing letter", e.getMessage());
        }
    }

    private void editProfile() {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            this.root = fxmlLoader.load();

            final var byEmail = usersService.getCurrentUser(Empty.newBuilder().build());

            final EditController controller = fxmlLoader.getController();
            controller.getEmail().setText(byEmail.getEmail());
            controller.getFirstname().setText(byEmail.getFirstname());
            controller.getLastname().setText(byEmail.getLastname());
            controller.getUsername().setText(byEmail.getUsername());
            stage = new Stage();
            scene = new Scene(this.root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/edit.css")).toExternalForm());
            stage.centerOnScreen();
            stage.widthProperty().addListener((_, _, _) -> this.stage.centerOnScreen());
            stage.heightProperty().addListener((_, _, _) -> this.stage.centerOnScreen());
            stage.setScene(this.scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "Edit Profile", e.getMessage());
        }
    }

    public void addToBox(Button button, int index, TypeOfLetter choice) {
        button.setOnMouseClicked(_ -> {
            resetButtonStyles();
            hboxInsideInboxes.getChildren().clear();
            button.getStyleClass().add(SELECTED);

            var letters = typeOfLetterChoices.get(choice).addToBox(index, emailOfAuthUser.getText());

            Optional.of(letters)
                    .filter(list -> !list.isEmpty())
                    .ifPresentOrElse(
                            list -> list.forEach(letter -> addLetterToUI(letter, index)),
                            () -> {
                                final Text emptyText = new Text("No conversations in " + button.getText());
                                emptyText.setStyle("-fx-fill: #5f6368; -fx-font-size: 14px; -fx-padding: 20;");
                                final VBox container = new VBox(emptyText);
                                container.setAlignment(Pos.CENTER);
                                container.setPadding(new Insets(30));
                                hboxInsideInboxes.getChildren().add(container);
                            }
                    );
        });
    }

    public void addLetterToUI(Letter letter, int function) {
        final HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setId(letter.getId());

        hBox.getStyleClass().add("email-list-item");

        if (letter.getWatched()) {
            hBox.getStyleClass().add("email-read");
        } else {
            hBox.getStyleClass().add("email-unread");
        }

        final CheckBox checkBox = new CheckBox();

        final TextField mainTextField = LetterUIFactory.createTextField(letter, function);
        mainTextField.setEditable(false);
        mainTextField.getStyleClass().add("email-text-field");

        HBox.setHgrow(mainTextField, Priority.ALWAYS);
        mainTextField.setMaxWidth(Double.MAX_VALUE);

        final TextField dateTextField = new TextField();
        dateTextField.setEditable(false);
        dateTextField.setText(FieldUtil.refractorDate(letter.getCreatedAt()));
        dateTextField.getStyleClass().add("email-date-field");
        dateTextField.setMinWidth(100);
        dateTextField.setPrefWidth(100);

        checkBox.setOnAction(_ -> {
            if(checkBox.isSelected()) {
                hBox.setStyle("-fx-background-color: #c2dbff;");
            } else {
                hBox.setStyle("");
            }
            updateDeleteButtonVisibility();
        });

        mainTextField.setOnMouseClicked(_ -> {
            if (!checkBox.isSelected()) handleTextFieldClick(letter.getId(), function);
        });
        dateTextField.setOnMouseClicked(e -> {
            if (!checkBox.isSelected()) handleTextFieldClick(letter.getId(), function);
        });

        hBox.getChildren().addAll(checkBox, mainTextField, dateTextField);
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
        try {
            List<HBox> selectedBoxes = new LinkedList<>();
            hboxInsideInboxes.getChildren().forEach(node -> {
                if (node instanceof HBox hBox) {
                    CheckBox checkBox = (CheckBox) hBox.getChildren().getFirst();
                    if (checkBox.isSelected()) selectedBoxes.add(hBox);
                }
            });

            var email = emailOfAuthUser.getText();
            selectedBoxes.forEach(hBox -> {
                final String letterId = hBox.getId();
                switch (typeOfLetter) {
                    case SPAM, GARBAGE -> letterMoveService.setLetterType(SetLetterTypeRequest.newBuilder()
                            .setEmail(email)
                            .setLetterId(letterId)
                            .setType(typeOfLetter)
                            .build());
                    default -> letterService.deleteLetterById(LetterIdRequest.newBuilder().setId(letterId).build());
                }
            });

            hboxInsideInboxes.getChildren().removeAll(selectedBoxes);
            spambutton.setVisible(false);
            deleteButton.setVisible(false);
        } catch (Exception e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "Change letter position", e.getMessage());
        }
    }

    private void handleTextFieldClick(String letterId, int function) {
        if (openStages.containsKey(letterId)) {
            var existingStage = openStages.get(letterId);
            if (existingStage.isShowing()) {
                existingStage.requestFocus();
                return;
            } else openStages.remove(letterId);
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("checkLetter.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            root = fxmlLoader.load();
            final var letter1 = letterService.findById(LetterIdRequest.newBuilder().setId(letterId).build());
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
            updateInboxCounter();
            stage.show();
        } catch (IOException e) {
            AlertWindow.showAlert(Alert.AlertType.ERROR, "Field", e.getMessage());
        }
    }

    private void updateInboxCounter() {
        inboxCount.setText(String.valueOf(letterService.countOfInboxesLetter(
                LetterByEmail.newBuilder().setByEmail(emailOfAuthUser.getText()).build()).getCount()
        ));
    }
}
