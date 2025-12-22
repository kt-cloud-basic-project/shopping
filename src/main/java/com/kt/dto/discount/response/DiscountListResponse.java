package com.kt.dto.discount.response;

import com.kt.domain.discount.DiscountTargetType;
import com.kt.domain.discount.DiscountType;
import com.querydsl.core.annotations.QueryProjection;

public record DiscountListResponse(
	Long id,
	String name,
	DiscountTargetType targetType,
	DiscountType type,
	Boolean isCombinable,
	Long value
) {

	@QueryProjection
	public DiscountListResponse {
	}
}
