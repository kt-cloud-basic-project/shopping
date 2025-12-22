package com.kt.domain.discount.policy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedDiscountPolicy implements DiscountPolicy {
	private final Long discountValue;

	@Override
	public Long calcDiscountPrice(Long originalPrice) {
		if (originalPrice == null || originalPrice <= 0) {
			return 0L;
		}

		return Math.min(discountValue, originalPrice);
	}

	@Override
	public Long calcDiscountedPrice(Long originalPrice) {
		if (originalPrice == null || originalPrice <= 0) {
			return 0L;
		}

		return Math.max(0, originalPrice - discountValue);
	}
}
