package com.kt.dto.order.response;

import java.util.List;
import java.util.Map;

import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.dto.discount.response.DiscountResult;
import com.querydsl.core.annotations.QueryProjection;

public record OrderListResponse(
	Long id,
	OrderStatus orderStatus,
	List<OrderItemResponse> items
	) {

	public static OrderListResponse from(Order order, Map<Long, DiscountResult> orderProductDiscounts) {
		return new OrderListResponse(
		order.getId(),
		order.getOrderStatus(),
		order.getOrderProducts()
			.stream()
			.map(op -> {
				DiscountResult discountResult = orderProductDiscounts.get(op.getId());
				return OrderItemResponse.from(op, discountResult);
			})
			.toList()
		);
}
	@QueryProjection
	public OrderListResponse {
	}
}
