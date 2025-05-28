package org.parent.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.practice.seeyaa.configuration.fileconfiguration.PathMultipartFile;
import org.practice.seeyaa.enums.FileSize;
import org.practice.seeyaa.models.request.LetterRequestDto;
import org.practice.seeyaa.security.SecurityService;
import org.practice.seeyaa.service.LetterService;
import org.practice.seeyaa.service.StorageService;
import org.practice.seeyaa.service.impl.StorageServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.practice.seeyaa.util.AlertWindow.showAlert;


@Component
public class SendLetterController {
    @FXML
    private Button attachFile;
    @FXML
    private TextField hiding;
    @FXML
    private Button sendLetter;
    @FXML
    private TextArea text;
    @FXML
    private TextField toWhom;
    @FXML
    private TextField topic;
    @FXML
    private Label attachmentLabel;

    private final LetterService letterService;
    private final StorageService storageService;
    private final SecurityService securityService;

    private Stage stage;

    private final List<File> selectedFiles = new ArrayList<>();

    public SendLetterController(LetterService letterService, StorageServiceImpl storageService, SecurityService securityService) {
        this.letterService = letterService;
        this.storageService = storageService;
        this.securityService = securityService;
    }

    @FXML
    public void initialize() {
        attachFile.setOnAction(event -> {
            selectedFiles.clear();
            attachFile();
        });
    }

    @FXML
    public void sendLetter(ActionEvent event) {
        final var scene = ((Node) event.getSource()).getScene();
        Platform.runLater(() -> {
            text.setDisable(true);
            attachFile.setDisable(true);
            sendLetter.setDisable(true);
            toWhom.setDisable(true);
            topic.setDisable(true);
            scene.setCursor(Cursor.WAIT);
        });

        Task<Void> uploadTask = new Task<>() {
            @Override
            protected Void call() {
                LetterRequestDto request = new LetterRequestDto(
                        text.getText(),
                        topic.getText(),
                        toWhom.getText(),
                        securityService.getCurrentUserEmail());
                final var savedLetter = letterService.sendLetter(request);

                for (File file : selectedFiles) {
                    MultipartFile multipartFile = new PathMultipartFile(file);
                    storageService.uploadFile(multipartFile, savedLetter);
                }
                return null;
            }
        };

        uploadTask.setOnSucceeded(e ->
                Platform.runLater(() -> {
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                    scene.setCursor(Cursor.DEFAULT);
                }));
        uploadTask.setOnFailed(e -> {
            Platform.runLater(() -> scene.setCursor(Cursor.DEFAULT));

            showAlert("Upload Failed", uploadTask.getException().getMessage());
        });

        new Thread(uploadTask).start();
    }

    private void attachFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        final List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            var scene = attachFile.getScene();
            Platform.runLater(() -> scene.setCursor(Cursor.WAIT));

            Task<Void> fileProcessingTask = new Task<>() {
                @Override
                protected Void call() {
                    selectedFiles.clear();
                    for (File file : files) {
                        if (file.length() > FileSize.ONE_GB.getSize()) {
                            showAlert("File Too Large", "File exceeds %d MB limit: %.2f MB"
                                    .formatted(FileSize.ONE_GB.getSize(), file.length() / (1024.0 * 1024.0)));
                            continue;
                        }
                        selectedFiles.add(file);
                    }
                    return null;
                }
            };

            fileProcessingTask.setOnSucceeded(event -> {
                updateAttachmentLabel();
                scene.setCursor(Cursor.DEFAULT);
            });

            fileProcessingTask.setOnFailed(event -> {
                showAlert("Error", "Failed to process files.");
                scene.setCursor(Cursor.DEFAULT);
            });

            new Thread(fileProcessingTask).start();
        }
    }

    private void updateAttachmentLabel() {
        if (!selectedFiles.isEmpty())
            attachmentLabel.setText("Attachments: " + selectedFiles.size());
        else attachmentLabel.setText("");
    }

    public void setHiding(String hidingEmail) {
        hiding.setText(hidingEmail);
    }

}
