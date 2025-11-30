package com.kt.dto.user.request;

import jakarta.validation.constraints.NotNull;

public record UserChangePassword(
        @NotNull
        String password
){

}
