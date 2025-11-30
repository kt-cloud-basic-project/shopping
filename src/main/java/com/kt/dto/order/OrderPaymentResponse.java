package com.kt.dto.order;

import com.kt.domain.payment.PaymentType;

public record OrderPaymentResponse (
	PaymentType paymentType,
	Long totalPrice,
	Long deliveryFee,
	Long finalPrice
) {
}