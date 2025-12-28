package com.kt.event;

public record ProductDiscountEvent(
	Long discountId,
	Long productId,
	String productName,
	String discountName,
	Long discountValue
) {
}
