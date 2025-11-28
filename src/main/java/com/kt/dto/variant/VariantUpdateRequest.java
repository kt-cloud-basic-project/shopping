package com.kt.dto.variant;

import jakarta.validation.constraints.NotBlank;

public record VariantUpdateRequest(
	@NotBlank(message = "상세값 입력은 필수입니다")
	String detail
) {
}
