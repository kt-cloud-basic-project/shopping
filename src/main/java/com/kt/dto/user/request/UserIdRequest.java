package com.kt.dto.user.request;

import jakarta.validation.constraints.NotBlank;

public record UserIdRequest(
        @NotBlank
        String userId
) {
}
