package com.kt.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record ProductUpdateRequest(
	@Schema(description = "수정할 이름", example = "수정한 상품")
	String name,
	@Schema(description = "수정할 설명", example = "수정한 내용입니다")
	String description,
	@Schema(description = "수정할 가격", example = "20000")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다")
	Long price,
	@Schema(description = "수정할 수량", example = "200")
	@Min(value = 0, message = "수량은 0 이상이어야 합니다")
	Long stock
) {
}