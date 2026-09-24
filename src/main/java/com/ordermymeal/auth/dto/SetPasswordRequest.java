package com.ordermymeal.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SetPasswordRequest(
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must contain 8 to 100 characters")
        String password
) {
}
