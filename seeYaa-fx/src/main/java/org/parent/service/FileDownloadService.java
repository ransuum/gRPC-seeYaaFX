package org.parent.service;

import com.seeYaa.proto.email.Files;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.util.function.Consumer;

public interface FileDownloadService {
    Task<Files> createFileLoadTask(int fileId);
    Task<Void> createDownloadTask(Files completeFile, Stage stage,
                                  Runnable onDone, Consumer<Exception> onError);
}
