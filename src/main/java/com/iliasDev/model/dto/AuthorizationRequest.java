package com.iliasDev.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorizationRequest(
        @Email(message = "Login must be a valid email address")
        @NotBlank(message = "Login cannot be empty")
        @Size(max = 64, message = "Login must be less than 65 characters")
        String login,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
        String password
) {
}
