package com.kt.event;

public record WishlistAddedEvent(
	Long userId,
	Long productId
) {
}
