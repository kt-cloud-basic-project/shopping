package com.kt.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record UserAdminChangePassword(
        @NotNull
        String userId,
        @NotNull
        String password
) {
}
