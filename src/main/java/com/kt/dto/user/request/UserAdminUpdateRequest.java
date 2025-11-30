package com.kt.dto.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserAdminUpdateRequest(
        @NotNull
        String userId,
        String name,
        @Pattern(regexp = "^$|^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
        String mobile,
        @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email
) {
}
