package org.parent.controller;

import com.seeYaa.proto.email.Answer;
import com.seeYaa.proto.email.Files;
import com.seeYaa.proto.email.Letter;
import com.seeYaa.proto.email.service.storage.LetterIdRequest;
import com.seeYaa.proto.email.service.storage.FileMetadata;
import com.seeYaa.proto.email.service.storage.StorageServiceGrpc;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.parent.service.FileDownloadService;
import org.parent.ui.AnswerRowFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import module java.base;

import static org.parent.util.AlertWindow.showAlert;

@Component
@Slf4j
public class CheckMyLetterController {
    @FXML private Label email;
    @FXML private Label firstNameLast;
    @FXML private VBox answers;
    @FXML private VBox filesContainer;
    @FXML private TextArea textOfLetter;
    @FXML private Label topic;
    @Setter private String currentEmail;

    private Letter letterDto;
    private Stage stage;

    private final ConfigurableApplicationContext springContext;
    private final StorageServiceGrpc.StorageServiceBlockingStub storageService;
    private final AIController aiController;
    private final FileDownloadService fileDownloadService;

    public CheckMyLetterController(ConfigurableApplicationContext springContext, StorageServiceGrpc.StorageServiceBlockingStub storageService,
                                   AIController aiController, FileDownloadService fileDownloadService) {
        this.springContext = springContext;
        this.storageService = storageService;
        this.aiController = aiController;
        this.fileDownloadService = fileDownloadService;
    }

    public void quit(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void helpToUnderstandText() {
        try {
            final var fxmlLoader = new FXMLLoader(getClass().getResource("ai-response.fxml"));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Parent root = fxmlLoader.load();

            final var prompt = String.format("""
                    Please analyze this letter and provide insights on language that wrote:
                    
                    Topic: %s
                    
                    Content:
                    %s
                    
                    Please provide:
                    1. A summary of the main points
                    2. The key message or request
                    3. Suggested points to consider when responding
                    """, topic.getText(), textOfLetter.getText());

            aiController.setPrompt(prompt);

            final var aiStage = new Stage();
            final var scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/checkMyLetter.css")).toExternalForm());
            aiStage.setScene(scene);
            aiStage.setTitle("AI Analysis");
            aiStage.initModality(Modality.APPLICATION_MODAL);
            aiStage.show();
        } catch (IOException e) {
            log.error("Error opening AI response window", e);
            showAlert(Alert.AlertType.ERROR, "Ai", "Error opening AI response window: " + e.getMessage());
        }
    }

    @FXML
    public void answer() throws IOException {
        answerOnLetter();
    }

    public void setLetter(Letter letter1, int function) {
        this.letterDto = letter1;
        setLetterContent(letter1.getTopic(), letter1.getText(),
                (function == 1) ? letter1.getUserBy().getEmail()
                        : letter1.getUserTo().getEmail(),
                (function == 1) ? letter1.getUserBy().getFirstname() + " " + letter1.getUserBy().getLastname()
                        : letter1.getUserTo().getFirstname() + " " + letter1.getUserTo().getLastname());

        loadAnswers();

        loadFileMetadataAsync();
    }

    private void setLetterContent(String topic, String text, String byEmail, String fullName) {
        this.topic.setText(topic);
        this.textOfLetter.setText(text);
        this.email.setText("Email: " + byEmail);
        this.firstNameLast.setText(fullName);
    }

    private void loadAnswers() {
        answers.getChildren().clear();
        answers.setSpacing(10);
        answers.setPadding(new Insets(10));

        final List<Answer> sorted = letterDto.getAnswersList();

        for (var answer : sorted) {
            var answerRow = AnswerRowFactory.createAnswerRow(answer, textOfLetter::setText);
            answers.getChildren().add(answerRow);
        }
    }

    private void loadFileMetadataAsync() {
        filesContainer.getChildren().clear();
        var loadingLabel = new Label("Loading file information...");
        var progress = new ProgressIndicator();
        var loader = new HBox(10, progress, loadingLabel);
        filesContainer.getChildren().add(loader);

        Task<List<FileMetadata>> task = new Task<>() {
            @Override
            protected List<FileMetadata> call() {
                return storageService.getFileMetadataByLetterId(LetterIdRequest.newBuilder()
                                .setLetterId(letterDto.getId())
                                .build())
                        .getMetadataList();
            }
        };

        task.setOnSucceeded(_ -> Platform.runLater(() -> showFiles(task.getValue())));
        task.setOnFailed(_ -> showAlert(Alert.AlertType.ERROR, "Loading data",
                "Failed to load files metadata: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void showFiles(List<FileMetadata> files) {
        filesContainer.getChildren().clear();

        if (CollectionUtils.isEmpty(files)) {
            Label noFiles = new Label("No attachments");
            noFiles.setStyle("-fx-text-fill: #9aa0a6; -fx-font-style: italic;");
            filesContainer.getChildren().add(noFiles);
            return;
        }

        for (FileMetadata meta : files) {
            final HBox fileCard = new HBox(10);
            fileCard.setAlignment(Pos.CENTER_LEFT);
            fileCard.getStyleClass().add("file-card");

            final ImageView icon = new ImageView(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("images/attachment.png"))));
            icon.setFitWidth(24);
            icon.setFitHeight(24);

            final VBox info = new VBox(2);
            Label nameLabel = new Label(meta.getName());
            nameLabel.getStyleClass().add("file-name");

            final Label sizeLabel = new Label(meta.getSize() + " bytes");
            sizeLabel.getStyleClass().add("file-size");

            info.getChildren().addAll(nameLabel, sizeLabel);

            fileCard.setOnMouseClicked(_ -> fileDownload(meta));

            fileCard.getChildren().addAll(icon, info);
            filesContainer.getChildren().add(fileCard);
        }
    }

    private void fileDownload(FileMetadata meta) {
        Task<Files> loadTask = fileDownloadService.createFileLoadTask(meta.getId());
        loadTask.setOnSucceeded(e -> {
            final Files completeFile = loadTask.getValue();
            Task<Void> downloadTask = fileDownloadService.createDownloadTask(
                    completeFile, stage,
                    () -> {},
                    _ -> {}
            );
            new Thread(downloadTask).start();
        });
        loadTask.setOnFailed(e ->
                showAlert(Alert.AlertType.ERROR,"File download", loadTask.getException().getMessage()));
        new Thread(loadTask).start();
    }

    private void answerOnLetter() throws IOException {
        var fxmlLoader = new FXMLLoader(getClass().getResource("answer.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();

        final AnswerController controller = fxmlLoader.getController();
        controller.setIdOfLetter(letterDto.getId());
        controller.setEmailBy(currentEmail);

        stage = new Stage();
        final Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("static/answer.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Answer");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
