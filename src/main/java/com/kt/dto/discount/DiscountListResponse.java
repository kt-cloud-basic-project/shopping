package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;
import com.querydsl.core.annotations.QueryProjection;

public record DiscountListResponse(
	Long membershipId,
	String membershipName,
	Long id,
	String name,
	DiscountType type,
	Integer value
) {

	@QueryProjection
	public DiscountListResponse {
	}
}
