package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRequest(
        @Schema(description = "수정할 이름", example = "김케클")
        String name,

        @Schema(description = "수정할 번호", example = "010-0401-0401")
        @Pattern(regexp = "^$|^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
        String mobile,

        @Schema(description = "수정할 이메일", example = "Test@kmail.com")
        @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email
) {
}