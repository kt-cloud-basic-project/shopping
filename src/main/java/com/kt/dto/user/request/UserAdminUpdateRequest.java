package com.kt.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserAdminUpdateRequest(
        @Schema(description = "로그인 비밀번호", example = "Test@1234")
        @NotNull
        String userId,

        @Schema(description = "수정할 이름", example = "김케클")
        String name,

        @Schema(description = "수정할 이메일", example = "Test@kmail.com")
        @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email,

        @Schema(description = "수정할 핸드폰 번호", example = "010-0401-0401")
        @Pattern(regexp = "^$|^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
        String mobile

) {
}
