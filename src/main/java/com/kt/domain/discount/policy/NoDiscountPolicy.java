package com.kt.domain.discount.policy;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NoDiscountPolicy implements DiscountPolicy {

	@Override
	public Long calcDiscountPrice(Long originalPrice) {
		return 0L;
	}

	@Override
	public Long calcDiscountedPrice(Long originalPrice) {
		return originalPrice != null ? originalPrice : 0L;
	}
}
