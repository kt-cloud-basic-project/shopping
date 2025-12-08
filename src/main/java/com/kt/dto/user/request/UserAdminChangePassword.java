package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserAdminChangePassword(
        @Schema(description = "관리자 로그인 아이디", example = "testAdmin")
        @NotNull
        String userId,

        @Schema(description = "관리자 로그인 비밀번호", example = "Test@1234")
        @NotNull
        String password
) {
}
