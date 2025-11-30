package com.kt.dto.order.response;

import com.kt.domain.orderproduct.OrderProduct;

public record OrderItemResponse(
	Long id,
	String name,
	Long count,
	Long price
) { public static OrderItemResponse from(OrderProduct orderProduct) {
	return new OrderItemResponse(
		orderProduct.getId(),
		orderProduct.getProduct().getName(),
		orderProduct.getCount(),
		orderProduct.getProduct().getPrice()
	);
}
}
