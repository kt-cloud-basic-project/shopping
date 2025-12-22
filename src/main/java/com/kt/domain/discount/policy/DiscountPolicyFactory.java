package com.kt.domain.discount.policy;

import com.kt.domain.discount.Discount;

public class DiscountPolicyFactory {
	public static DiscountPolicy getDiscountPolicy(Discount discount) {
		if (discount == null) {
			return new NoDiscountPolicy();
		}

		return switch (discount.getType()) {
			case FIXED_AMOUNT -> new FixedDiscountPolicy(discount.getValue());
			case PERCENTAGE -> new PercentageDiscountPolicy((int)discount.getValue());
		};
	}
}
