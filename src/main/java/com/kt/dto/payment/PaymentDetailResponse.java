package com.kt.dto.payment;

import java.time.LocalDateTime;

import com.kt.domain.order.OrderStatus;

public record PaymentDetailResponse(
	Long id, // 결제 ID
	Long orderId, // 주문 ID
	String userLoginId, // 결제자 아이디(직접 결제한 유저는 로그인한 유저)
	String userName, // 결제자 이름
	OrderStatus orderStatus, // 주문 상태
	String paymentTypeName, // 결제수단이름
	Long totalPrice, // 상품 총 가격
	Long deliveryFee, // 배송비
	Long finalPrice, // 최종 결제 가격
	LocalDateTime createdAt // 결제 일시
) {

}
