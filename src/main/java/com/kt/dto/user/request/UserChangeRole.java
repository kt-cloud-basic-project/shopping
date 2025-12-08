package com.kt.dto.user.request;

import com.kt.domain.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserChangeRole(
        @Schema(description = "수정대상 아이디", example = "testUser")
        @NotNull
        String userId,

        @Schema(description = "권한", example = "ADMIN")
        @NotNull
        Role role
) {
}
