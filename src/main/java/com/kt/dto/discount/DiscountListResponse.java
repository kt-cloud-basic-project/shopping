package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;
import com.querydsl.core.annotations.QueryProjection;

public record DiscountListResponse(
		Long id,
		String name,
		DiscountType type,
		Integer value,
		Long membershipId,
		String membershipName
) {

	@QueryProjection
	public DiscountListResponse {
	}
}
