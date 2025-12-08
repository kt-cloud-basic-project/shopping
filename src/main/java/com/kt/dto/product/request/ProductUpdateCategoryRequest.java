package com.kt.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ProductUpdateCategoryRequest(
	@Schema(description = "수정할 카테고리 id", example = "1")
	Long categoryId
) {
}
