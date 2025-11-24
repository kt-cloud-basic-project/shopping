package com.kt.dto.product;

import jakarta.validation.constraints.Min;

public record ProductUpdateRequest(
	String name,
	String description,
	@Min(value = 0, message = "가격은 0 이상이어야 합니다")
	Long price,
	@Min(value = 0, message = "수량은 0 이상이어야 합니다")
	Long stock
) {
}