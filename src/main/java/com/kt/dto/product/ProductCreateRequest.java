package com.kt.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest (
	@NotBlank(message = "상품 이름은 필수 값입니다")
	String name,
	@NotBlank(message = "상품 설명은 필수 값입니다")
	String description,
	@NotNull(message = "상품 가격은 필수 값입니다")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다")
	Long price,
	@NotNull(message = "상품 수량은 필수 값입니다")
	@Min(value = 0, message = "수량은 0 이상이어야 합니다")
	Long stock,
	@NotNull(message = "상품 카테고리 id는 필수 값입니다")
	Long categoryId
) {
}