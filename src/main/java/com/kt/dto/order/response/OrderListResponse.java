package com.kt.dto.order.response;

import java.util.List;

import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;

public record OrderListResponse(
	Long id,
	OrderStatus orderStatus,
	List<OrderItemResponse> items
) { public static OrderListResponse from(Order order) {
	return new OrderListResponse(
		order.getId(),
		order.getOrderStatus(),
		order.getOrderProducts()
			.stream()
			.map(OrderItemResponse::from)
			.toList()
	);
}
}
