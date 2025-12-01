package com.kt.dto.order;

import java.time.LocalDate;
import java.util.List;

import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.payment.Payment;

public record OrderDetailResponse(
	OrderStatus orderStatus,
	String receiverName,
	String receiverPhone,
	String receiverAddress,
	LocalDate orderedAt,
	List<OrderProductResponse> products
) {
	public static OrderDetailResponse from(Order order, List<OrderProductResponse> products) {
		return new OrderDetailResponse(
			order.getOrderStatus(),
			order.getReceiverName(),
			order.getReceiverPhone(),
			order.getReceiverAddress(),
			order.getCreatedAt().toLocalDate(),
			products
		);
	}
}