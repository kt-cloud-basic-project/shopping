package com.kt.dto.variant;

import com.kt.domain.variant.VariantType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VariantCreateRequest(
	@NotNull(message = "옵션 타입 선택은 필수입니다")
	VariantType type,
	@NotBlank(message = "옵션 상세값 입력은 필수입니다")
	String detail
) {
}
