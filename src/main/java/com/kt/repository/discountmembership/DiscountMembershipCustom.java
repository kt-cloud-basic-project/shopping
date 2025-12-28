package com.kt.repository.discountmembership;

import com.kt.dto.discount.response.DiscountMembershipDetailResponse;

public interface DiscountMembershipCustom {
	DiscountMembershipDetailResponse findDiscountDetailByDiscountId(Long userId);
}
