package com.kt.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserChangePassword(
        @NotNull
        String password
){

}
