package com.kt.repository.discountproduct;

import com.kt.dto.discount.response.DiscountProductDetailResponse;

public interface DiscountProductCustom {
	DiscountProductDetailResponse findDiscountDetailByDiscountId(Long userId);
}
