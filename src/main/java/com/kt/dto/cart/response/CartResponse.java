package com.kt.dto.cart.response;

import com.kt.domain.cart.Cart;

public record CartResponse(
	Long cartId,
	String name,
	Long variant,
	Integer productCount,
	Long productId
) {
	public static CartResponse from(Cart cart) {
		return new CartResponse(
			cart.getId(),
			cart.getProduct().getName(),
			cart.getVariant(),
			cart.getProductCount(),
			cart.getProduct().getId()
		);
	}
}
