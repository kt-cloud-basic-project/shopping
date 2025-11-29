package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscountUpdateRequest(

	@NotBlank(message = "할인 이름은 필수입니다")
	String name,

	@NotNull(message = "할인 타입은 필수입니다")
	DiscountType type,

	@NotNull(message = "할인 값은 필수입니다")
	@Min(value = 1, message = "할인 값은 1 이상이어야 합니다")
	Integer value
) {
}
