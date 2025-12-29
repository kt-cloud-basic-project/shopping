package com.kt.dto.discount.request;

import com.kt.domain.discount.DiscountTargetType;
import com.kt.domain.discount.DiscountType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscountCreateRequest(

	@Schema(description = "타겟 타입", example = "membership")
	@NotNull(message = "할인 타겟 타입은 필수입니다")
	DiscountTargetType targetType,

	@Schema(description = "제품 또는 멤버십 ID", example = "10")
	@NotNull(message = "제품 또는 멤버십 ID는 필수입니다")
	@Min(value = 1, message = "ID 값은 1 이상이어야 합니다")
	Long targetId,

	@Schema(description = "할인 이름", example = "SILVER 회원 10% 할인")
	@NotBlank(message = "할인 이름은 필수입니다")
	String name,

	@Schema(description = "할인 타입", example = "PERCENTAGE")
	@NotNull(message = "할인 타입은 필수입니다")
	DiscountType type,

	@Schema(description = "할인 중복 적용 가능 여부", example = "true")
	@NotNull(message = "할인 중복 적용 가능 여부는 필수입니다")
	Boolean isCombinable,

	@Schema(description = "할인 값", example = "10")
	@NotNull(message = "할인 값은 필수입니다")
	@Min(value = 1, message = "할인 값은 1 이상이어야 합니다")
	Long value
) {
}
