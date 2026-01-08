package org.parent.controller;

import com.google.protobuf.ByteString;
import com.seeYaa.proto.email.FileType;
import com.seeYaa.proto.email.service.letter.LetterRequest;
import com.seeYaa.proto.email.service.letter.LetterServiceGrpc;
import com.seeYaa.proto.email.service.storage.StorageServiceGrpc;
import com.seeYaa.proto.email.service.storage.UploadFileRequest;
import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.parent.configuration.file.MultipartFile;
import org.parent.configuration.file.PathMultipartFile;
import org.parent.configuration.file.size.FileSize;
import org.parent.grpcserviceseeyaa.security.SecurityService;
import org.parent.util.AlertWindow;
import org.parent.util.ExceptionUtil;
import org.springframework.stereotype.Component;

import module java.base;

import static org.parent.util.AlertWindow.showAlert;

@Component
public class SendLetterController {
    @FXML private Button attachFile;
    @FXML private TextField hiding;
    @FXML private Button sendLetter;
    @FXML private TextArea text;
    @FXML private TextField toWhom;
    @FXML private TextField topic;
    @FXML private Label attachmentLabel;

    private final LetterServiceGrpc.LetterServiceBlockingStub letterService;
    private final StorageServiceGrpc.StorageServiceBlockingStub storageService;
    private final SecurityService securityService;

    private Stage stage;

    private final List<File> selectedFiles = new ArrayList<>();

    public SendLetterController(LetterServiceGrpc.LetterServiceBlockingStub letterService,
                                StorageServiceGrpc.StorageServiceBlockingStub storageService,
                                SecurityService securityService) {
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
        var scene = ((Node) event.getSource()).getScene();
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
            protected Void call() throws IOException {
                var savedLetter = letterService.sendLetter(LetterRequest.newBuilder()
                        .setText(text.getText())
                        .setTopic(topic.getText())
                        .setUserToEmail(toWhom.getText())
                        .setUserByEmail(securityService.getCurrentUserEmail())
                        .build());

                for (File file : selectedFiles) {
                    MultipartFile multipartFile = new PathMultipartFile(file);
                    storageService.uploadFile(UploadFileRequest.newBuilder()
                            .setLetterId(savedLetter.getId())
                            .setData(ByteString.copyFrom(multipartFile.getBytes()))
                            .setType(FileType.UNKNOWN)
                            .setName(multipartFile.getOriginalFilename())
                            .setSize(multipartFile.getSize())
                            .build());
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

        uploadTask.setOnFailed(e ->
                Platform.runLater(() -> {
                    scene.setCursor(Cursor.DEFAULT);
                    text.setDisable(false);
                    attachFile.setDisable(false);
                    sendLetter.setDisable(false);
                    toWhom.setDisable(false);
                    topic.setDisable(false);

                    final Throwable throwable = uploadTask.getException();
                    AlertWindow.showAlert(Alert.AlertType.WARNING, "Send Letter Failed", ExceptionUtil.getMessageFromError(throwable));
                }));

        new Thread(uploadTask).start();
    }


    private void attachFile() {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        var files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            var scene = attachFile.getScene();
            Platform.runLater(() -> scene.setCursor(Cursor.WAIT));

            Task<Void> fileProcessingTask = new Task<>() {
                @Override
                protected Void call() {
                    selectedFiles.clear();
                    for (File file : files) {
                        if (file.length() > FileSize.ONE_GB.getSize()) {
                            showAlert(Alert.AlertType.ERROR, "File Size", "File exceeds %d MB limit: %.2f MB"
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
                showAlert(Alert.AlertType.ERROR, "Can't attach files", "Failed to process files.");
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
