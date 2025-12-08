package com.kt.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartUpdateQuantityRequest(
	@Schema(description = "상품 수량", example = "3")
	@NotNull(message = "상품 수량은 필수 값입니다")
	@Positive(message = "상품 수량은 최소 1개 이상이어야 합니다")
	Integer productCount
) {
}
