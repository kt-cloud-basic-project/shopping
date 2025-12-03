package com.kt.dto.variant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record VariantUpdateRequest(
	@Schema(description = "수정할 상세 값", example = "L")
	@NotBlank(message = "상세값 입력은 필수입니다")
	String detail
) {
}
