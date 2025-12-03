package com.kt.dto.variant;

import com.kt.domain.variant.VariantType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VariantCreateRequest(
	@Schema(description = "옵션 타입", example = "SIZE")
	@NotNull(message = "옵션 타입 선택은 필수입니다")
	VariantType type,
	@Schema(description = "옵션 상세 값", example = "M")
	@NotBlank(message = "옵션 상세값 입력은 필수입니다")
	String detail
) {
}
