package com.kt.dto.discount.response;

public record DiscountInfo(
	String discountName,
	String discountType,
	Long discountPrice
) {
}
