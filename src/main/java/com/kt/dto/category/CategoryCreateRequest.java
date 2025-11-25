package com.kt.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
	@NotBlank(message = "상품 카테고리 타입 입력은 필수 값입니다")
	String type
) {
}
