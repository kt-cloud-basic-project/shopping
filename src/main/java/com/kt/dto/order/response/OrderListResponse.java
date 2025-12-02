package com.kt.dto.order.response;

import java.util.List;

import com.kt.domain.discount.Discount;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.querydsl.core.annotations.QueryProjection;

public record OrderListResponse(
	Long id,
	OrderStatus orderStatus,
	List<OrderItemResponse> items
	) {

	public static OrderListResponse from(Order order, Discount discount) {
		return new OrderListResponse(
		order.getId(),
		order.getOrderStatus(),
		order.getOrderProducts()
			.stream()
			.map(op -> OrderItemResponse.from(op, discount))
			.toList()
		);
}
	@QueryProjection
	public OrderListResponse {
	}
}
