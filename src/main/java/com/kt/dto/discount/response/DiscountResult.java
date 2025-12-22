package com.kt.dto.discount.response;

public record DiscountResult(
	Long originalPrice,
	Long discountPrice,
	Long discountedPrice
) {
	public static DiscountResult noDiscount(Long originalPrice) {
		return new DiscountResult(originalPrice, 0L, originalPrice);
	}
}
