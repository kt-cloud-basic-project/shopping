package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserLoginRequest(
            @Schema(description = "로그인 아이디", example = "testUser")
            @NotBlank
            String loginId,

            @Schema(description = "로그인 비밀번호", example = "Test@1234")
            @NotBlank
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^])[A-Za-z\\d!@#$%^]{8,}$")
            String password

    ) {
    }

