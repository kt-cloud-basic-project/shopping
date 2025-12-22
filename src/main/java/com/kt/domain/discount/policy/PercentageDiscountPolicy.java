package com.kt.domain.discount.policy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PercentageDiscountPolicy implements DiscountPolicy {
	private final Integer percentage;

	@Override
	public Long calcDiscountPrice(Long originalPrice) {
		if (originalPrice == null || originalPrice <= 0) {
			return 0L;
		}

		return originalPrice * percentage / 100;
	}

	@Override
	public Long calcDiscountedPrice(Long originalPrice) {
		return originalPrice - calcDiscountPrice(originalPrice);
	}
}
