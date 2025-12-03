package com.kt.dto.user.request;

import com.kt.domain.user.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UserCreateRequest (

            @Schema(description = "로그인 아이디", example = "testUser")
            @NotBlank
            String loginId,

            @Schema(description = "로그인 비밀번호", example = "Test@1234")
            @NotBlank
            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^])[A-Za-z\\d!@#$%^]{8,}$")
            String password,

            @Schema(description = "이름", example = "김케티")
            @NotBlank
            String name,

            @Schema(description = "이메일", example = "test@example.com")
            @NotBlank
            @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            String email,

            @Schema(description = "핸드폰", example = "010-1111-2222")
            @NotBlank
            @Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
            String mobile,

            @Schema(description = "성별", example = "MALE")
            @NotNull
            Gender gender,

            @Schema(description = "생일", example = "1992-05-30")
            @NotNull
            LocalDate birthday,

            @Schema(description = "맴버십")
            String membershipLevel

){
    }

