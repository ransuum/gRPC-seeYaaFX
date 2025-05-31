package org.parent.grpcserviceseeyaa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerRequestDto(@NotBlank(message = "Text is blank")
                               @Size(max = 5000, message = "Text is too long")
                               String textOfLetter) { }
