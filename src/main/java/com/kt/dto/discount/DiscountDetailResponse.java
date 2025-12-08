package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;

public record DiscountDetailResponse(
	Long membershipId,
	String membershipLevel,
	Long id,
	String name,
	DiscountType type,
	Integer value
) {
}
