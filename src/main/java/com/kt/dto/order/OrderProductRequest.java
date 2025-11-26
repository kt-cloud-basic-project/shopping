package com.kt.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductRequest(
	@NotNull(message = "상품 선택은 필수입니다")
	Long productId,
	@NotNull(message = "상품 수량 선택은 필수입니다")
	@Min(value = 0, message = "상품 수량은 1개 이상 선택해야 합니다")
	Long productCount,
	@NotNull(message = "상품 옵션 선택은 필수입니다")
	Long productVariantId
) {
}