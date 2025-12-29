package com.kt.event;

public record CartItemAddedEvent(
	Long userId,
	Long productId
) {
}
