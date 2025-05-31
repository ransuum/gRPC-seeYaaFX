package org.parent.grpcserviceseeyaa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.parent.grpcserviceseeyaa.validator.email.ValidEmailTag;

public record LetterRequestDto(
        @NotBlank(message = "Text is blank")
        @Size(max = 5000, message = "Text is too long")
        String text,

        @NotBlank(message = "Topic is blank")
        @Size(max = 62, message = "Topic is too long")
        String topic,

        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        @ValidEmailTag(tag = "seeyaa.com", message = "Email must end with @seeyaa.com")
        String userTo,

        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        @ValidEmailTag(tag = "seeYaa.com", message = "Email must end with @seeYaa.com")
        String userBy) {
}
