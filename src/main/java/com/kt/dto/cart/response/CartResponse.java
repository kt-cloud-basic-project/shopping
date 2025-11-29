package com.kt.dto.cart.response;

import com.kt.domain.cart.Cart;

public record CartResponse(
	Long cartId,
	String name,
	Long variantId,
	Integer productCount,
	Long price, // 원가
	Long discountAmount, // 할인 금액
	Long discountedPrice // 할인된 최종 금액
) {
	public static CartResponse from(Cart cart, Long discountAmount, Long discountedPrice) {
		return new CartResponse(
			cart.getId(),
			cart.getProduct().getName(),
			cart.getVariantId(),
			cart.getProductCount(),
			cart.getProduct().getPrice(),
			discountAmount,
			discountedPrice
		);
	}
}
