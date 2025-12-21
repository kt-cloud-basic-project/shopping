package com.kt.dto.discount.response;

import com.kt.domain.discount.DiscountType;
import com.querydsl.core.annotations.QueryProjection;

public record DiscountUserResponse(
	String userLoginId,
	String userName,
	String membershipLevel,
	DiscountType discountType,
	String discountName,
	Boolean isCombinable,
	Long discountValue
) {

	@QueryProjection
	public DiscountUserResponse{
	}
}
