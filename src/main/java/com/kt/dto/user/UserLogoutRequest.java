package com.kt.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UserLogoutRequest")
public record UserLogoutRequest(
        @NotBlank
        String refreshToken
) {
}
