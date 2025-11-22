package com.kt.dto.user;


public record UserLoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static UserLoginResponse of(String accessToken, String refreshToken) {
        return new UserLoginResponse(accessToken, refreshToken, "Bearer");
    }
}