package com.kt.dto.cart.response;

import com.kt.domain.cart.Cart;

public record CartResponse(
	Long cartId,
	String name,
	Long variantId,
	Long productId,
	Integer productCount,
	Long price, // 원가
	Long totalPrice, // 변경된 수량이 적용된 금액
	Long discountPrice, // 할인 금액
	Long discountedPrice // 할인된 최종 금액
) {
	public static CartResponse from(Cart cart, Long discountPrice, Long discountedPrice) {
		return new CartResponse(
			cart.getId(),
			cart.getProduct().getName(),
			cart.getVariantId(),
			cart.getProduct().getId(),
			cart.getProductCount(),
			cart.getProduct().getPrice(),
			cart.getTotalPrice(),
			discountPrice,
			discountedPrice
		);
	}
}
