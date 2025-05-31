package org.parent.grpcserviceseeyaa.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.parent.grpcserviceseeyaa.validator.email.ValidEmailTag;

public record SignUpRequestDto(
        @Valid
        @Email(message = "Isn't email")
        @NotBlank(message = "Email is blank")
        @Size(max = 40, message = "Email is too long")
        @ValidEmailTag(tag = "seeYaa.com", message = "Email must end with @seeYaa.com")
        String email,

        @NotBlank(message = "Username is blank")
        @Size(max = 14, message = "Username is too long")
        String username,

        @Valid
        @NotBlank(message = "Password is blank")
        @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
        String password,

        @NotBlank(message = "firstname is blank")
        @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect first name")
        String firstname,

        @NotBlank(message = "lastname is blank")
        @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect last name")
        String lastname) {
}
