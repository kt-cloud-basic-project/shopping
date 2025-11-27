package com.kt.dto.paymenttype;

public record PaymentTypeListResponse(
	Long id,
	String name,
	Boolean isDeleted
) {
}
