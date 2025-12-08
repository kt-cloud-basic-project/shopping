package com.kt.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest (
	@Schema(description = "상품 이름", example = "새로운 상품")
	@NotBlank(message = "상품 이름은 필수 값입니다")
	String name,
	@Schema(description = "상품 설명", example = "상품 설명입니다")
	@NotBlank(message = "상품 설명은 필수 값입니다")
	String description,
	@Schema(description = "상품 가격", example = "10000")
	@NotNull(message = "상품 가격은 필수 값입니다")
	@Min(value = 0, message = "가격은 0 이상이어야 합니다")
	Long price,
	@Schema(description = "상품 수량", example = "100")
	@NotNull(message = "상품 수량은 필수 값입니다")
	@Min(value = 0, message = "수량은 0 이상이어야 합니다")
	Long stock,
	@Schema(description = "카테고리 id", example = "1")
	@NotNull(message = "상품 카테고리 id는 필수 값입니다")
	Long categoryId
) {
}