package com.kt.domain.discount.policy;


public interface DiscountPolicy {
	Long calcDiscountPrice(Long originalPrice);
	Long calcDiscountedPrice(Long originalPrice);
}
