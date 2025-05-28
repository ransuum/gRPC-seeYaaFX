package org.parent.controller;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.practice.seeyaa.util.AlertWindow.showAlert;

@Component
@Slf4j
public class AIController {
    @FXML private TextArea responseAi;
    @Setter private String prompt;

    @Value("${vertex.project_id}")
    private String projectId;
    @Value("${vertex.location}")
    private String locationId;
    @Value("${vertex.model_id}")
    private String modelId;

    @FXML
    public void initialize() {
        if (prompt != null) analyzeText();
    }

    private void analyzeText() {
        try (final VertexAI vertexAi = new VertexAI(projectId, locationId)) {
            final GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
            final List<SafetySetting> safetySettings = Arrays.asList(
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build());
            final GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName(modelId)
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
                            .setSafetySettings(safetySettings)
                            .build();


            final var content = ContentMaker.fromMultiModalData(prompt);
            final ResponseStream<GenerateContentResponse> responseStream =
                    model.generateContentStream(content);

            final StringBuilder fullResponse = new StringBuilder();


            responseStream.forEach(response -> {
                final String chunk = response.getCandidates(0)
                        .getContent()
                        .getParts(0)
                        .getText();
                fullResponse.append(chunk);

                Platform.runLater(() -> responseAi.setText(fullResponse.toString()));
            });
        } catch (IOException e) {
            log.info("Error occurred while generating the model: {}", e.getMessage());
            showAlert("AI didn't response", e.getMessage());
        }

    }
}
