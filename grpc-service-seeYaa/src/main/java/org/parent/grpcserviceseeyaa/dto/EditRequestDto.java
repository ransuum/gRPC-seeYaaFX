package org.parent.grpcserviceseeyaa.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EditRequestDto(
        @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect first name") String firstname,
        @Pattern(regexp = "^[\\p{L}\\p{M} ,.'-]+$", message = "Incorrect last name") String lastname,
        @Size(max = 14, message = "Username is too long") String username,

        @Size(min = 8, max = 30, message = "Password size should be from 9 to 30 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_-])[A-Za-z\\d@$!%*#?&_-]+$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
        String password) {
}
