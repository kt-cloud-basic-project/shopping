package com.kt.domain.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	ORDERED("주문 완료"),
	PAID("결제 완료"),
	PROCESSING("배송 전"),
	SHIPPED("배송 중"),
	DELIVERED("배송 완료"),

	CANCELLED("주문 취소 완료"),
	RETURN_REQUESTED("반품 요청"),
	RETURNED("반품 완료"),
	REFUND_REQUESTED("환불 요청"),
	REFUNDED("환불 완료");

	private final String description;
}
