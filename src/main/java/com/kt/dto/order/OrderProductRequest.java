package com.kt.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductRequest(

	@Schema(description = "상품 ID", example = "1")
	@NotNull(message = "상품 선택은 필수입니다")
	Long productId,

	@Schema(description = "상품 수량", example = "5")
	@NotNull(message = "상품 수량 선택은 필수입니다")
	@Min(value = 1, message = "상품 수량은 1개 이상 선택해야 합니다")
	Long productCount,

	@Schema(description = "상품 옵션", example = "2")
	@NotNull(message = "상품 옵션 선택은 필수입니다")
	Long productVariantId
) {
}