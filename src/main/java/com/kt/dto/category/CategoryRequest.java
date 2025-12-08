package com.kt.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
	@Schema(description = "카테고리 타입", example = "상의")
	@NotBlank(message = "상품 카테고리 타입 입력은 필수 값입니다")
	String type
) {
}
