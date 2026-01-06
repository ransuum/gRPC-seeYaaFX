package org.parent.ui;

import com.seeYaa.proto.email.service.storage.FileMetadata;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.parent.util.triofunction.Trio;

import java.util.function.Consumer;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileRowFactory {

    public static HBox createFileRow(
            FileMetadata fileMetadata,
            Consumer<FileMetadata> onDownloadClick,
            Supplier<Stage> stageSupplier
    ) {
        var fileRow = new HBox();
        fileRow.getStyleClass().add("file-row");
        fileRow.setSpacing(10);
        fileRow.setAlignment(Pos.CENTER_LEFT);
        fileRow.setMinHeight(40);

        var fileIcon = FileUiUtils.createFileTypeIcon(fileMetadata.getType());

        var fileInfo = new VBox(5);
        HBox.setHgrow(fileInfo, Priority.ALWAYS);

        var nameLabel = new Label(fileMetadata.getName());
        nameLabel.getStyleClass().add("file-name-label");
        nameLabel.setWrapText(true);

        var sizeLabel = new Label(FileUiUtils.formatFileSize(fileMetadata.getSize()));
        sizeLabel.getStyleClass().add("file-size-label");

        fileInfo.getChildren().addAll(nameLabel, sizeLabel);

        Trio<HBox, Button, ProgressIndicator> object = createObjects();
        var downloadButton = object.second();
        var progressIndicator = object.third();
        var buttonBox = object.first();

        downloadButton.setOnAction(e -> {
            progressIndicator.setVisible(true);
            downloadButton.setVisible(false);
            onDownloadClick.accept(fileMetadata);
        });

        stageSupplier.get().setOnCloseRequest(e -> {/*for file open while on app*/});

        fileRow.getChildren().addAll(fileIcon, fileInfo, buttonBox);
        return fileRow;
    }

    private static Trio<HBox, Button, ProgressIndicator> createObjects() {
        var downloadButton = new Button("↓");
        downloadButton.getStyleClass().add("download-button");

        var progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(24, 24);
        progressIndicator.setVisible(false);

        var buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(progressIndicator, downloadButton);
        return new Trio<>(buttonBox, downloadButton, progressIndicator);
    }
}
