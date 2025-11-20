package com.kt.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartUpdateRequest(
    @NotNull
    @Positive
    Integer productCount,
    @NotBlank
    String productOption,
    @NotNull
    Long productId
){
}
