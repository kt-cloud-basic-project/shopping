package com.kt.dto.payment;

public record PaymentTossCancelResponse(
		Long cancelAmount,
		String cancelReason,
		String canceledAt
) {
}
