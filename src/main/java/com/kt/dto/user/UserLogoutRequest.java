package com.kt.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserLogoutRequest")
public record UserLogoutRequest(
        String refreshToken
) {
}
