package com.kt.dto.order;

public record OrderUpdateRequest(
	String receiverName,
	String receiverPhone,
	Long receiverAddressId
) {
}
