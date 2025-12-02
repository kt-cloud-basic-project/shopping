package com.kt.dto.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
