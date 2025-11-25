package com.kt.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartCreateRequest(
    @NotNull(message = "상품 수량은 필수 값입니다")
    @Positive(message = "상품 수량은 최소 1개 이상이어야 합니다")
    Integer productCount,
    @NotBlank(message = "상품 옵션은 필수 값입니다")
    String productOption,
	@NotNull(message = "상품 수량은 필수 값입니다")
    Long productId
){
}
