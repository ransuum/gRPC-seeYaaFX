package org.parent.service;

import com.seeYaa.proto.email.Files;
import com.seeYaa.proto.email.service.storage.FileIdRequest;
import com.seeYaa.proto.email.service.storage.StorageServiceGrpc;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FileDownloadService {
    private final StorageServiceGrpc.StorageServiceBlockingStub serviceBlockingStub;

    public Task<Files> createFileLoadTask(int fileId) {
        return new Task<>() {
            @Override
            protected Files call() {
                return serviceBlockingStub.getFileById(FileIdRequest.newBuilder().setFileId(fileId).build());
            }
        };
    }

    public void downloadFile(Files completeFile, Stage stage, Runnable onSuccess, Consumer<Exception> onError) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName(completeFile.getName());

        File saveFile = fileChooser.showSaveDialog(stage);

        if (saveFile != null) {
            Task<Void> downloadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    copyToFile(completeFile.getData().toByteArray(), saveFile);
                    return null;
                }
            };

            downloadTask.setOnSucceeded(_ -> {
                if (onSuccess != null) onSuccess.run();
            });
            downloadTask.setOnFailed(_ -> {
                if (onError != null) onError.accept((Exception) downloadTask.getException());
            });

            new Thread(downloadTask).start();
        }
    }

    private void copyToFile(byte[] data, File outFile) throws IOException {
        try (InputStream is = new ByteArrayInputStream(data); var fos = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytesRead);
        }
    }
}
