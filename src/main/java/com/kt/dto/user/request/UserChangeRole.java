package com.kt.dto.user.request;

import com.kt.domain.user.Role;
import jakarta.validation.constraints.NotNull;

public record UserChangeRole(
        @NotNull
        String userId,
        @NotNull
        Role role
) {
}
