package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserChangePassword(
        @Schema(description = "수정할 비밀번호", example = "Test@12345")
        @NotNull
        String password
){

}
