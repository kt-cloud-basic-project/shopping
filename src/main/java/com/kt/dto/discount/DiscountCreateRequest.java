package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscountCreateRequest(

	@Schema(description = "할인 이름", example = "SILVER 회원 10% 할인")
	@NotBlank(message = "할인 이름은 필수입니다")
	String name,

	@Schema(description = "할인 타입", example = "PERCENTAGE")
	@NotNull(message = "할인 타입은 필수입니다")
	DiscountType type,

	@Schema(description = "할인 값", example = "10")
	@NotNull(message = "할인 값은 필수입니다")
	@Min(value = 1, message = "할인 값은 1 이상이어야 합니다")
	Integer value
) {
}
