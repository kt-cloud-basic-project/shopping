package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UserIdRequest(
        @Schema(description = "유저아이디", example = "testUser")
        @NotBlank
        String userId
) {
}
