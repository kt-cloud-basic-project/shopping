package com.kt.dto.discount;

import com.kt.domain.discount.DiscountType;
import com.querydsl.core.annotations.QueryProjection;

public record DiscountUserResponse(
	String userLoginId,
	String userName,
	String membershipLevel,
	DiscountType discountType,
	String discountName
) {

	@QueryProjection
	public DiscountUserResponse{
	}
}
