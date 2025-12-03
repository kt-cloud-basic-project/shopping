package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UserLogoutRequest")
public record UserLogoutRequest(
        @Schema(description = "리프레시 토큰", example = "refresh token을 토큰을 입력하세요")
        @NotBlank
        String refreshToken
) {
}
