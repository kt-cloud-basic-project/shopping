package com.kt.dto.cart.response;

import com.kt.domain.cart.Cart;

public record CartResponse(
	Long cartId,
	String name,
	Long variantId,
	Integer productCount,
	Long price
) {
	public static CartResponse from(Cart cart) {
		return new CartResponse(
			cart.getId(),
			cart.getProduct().getName(),
			cart.getVariantId(),
			cart.getProductCount(),
			cart.getProduct().getPrice()
		);
	}
}
