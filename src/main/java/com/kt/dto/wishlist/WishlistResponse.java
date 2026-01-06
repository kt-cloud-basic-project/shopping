package com.kt.dto.wishlist;

import java.time.LocalDateTime;

import com.kt.domain.wishlist.Wishlist;

public record WishlistResponse(
	Long id,
	Long productId,
	String productName,
	Long price,
	LocalDateTime createdAt
) {

	public static WishlistResponse from(Wishlist wishlist) {
		return new WishlistResponse(
			wishlist.getId(),
			wishlist.getProduct().getId(),
			wishlist.getProduct().getName(),
			wishlist.getProduct().getPrice(),
			wishlist.getCreatedAt()
		);
	}
}
