package com.kt.dto.order.response;

import com.kt.domain.discount.Discount;
import com.kt.domain.orderproduct.OrderProduct;
import com.querydsl.core.annotations.QueryProjection;

public record OrderItemResponse(
	Long id,
	String name,
	Long count,
	Long price, // 원 가격
	Long totalPrice, // 총 가격
	Long discountAmount, // 할인 금액
	Long discountedPrice // 할인 후 최종금액
) {
	public static OrderItemResponse from(OrderProduct orderProduct, Discount discount) {
		Long totalPrice = orderProduct.getProduct().getPrice() * orderProduct.getCount();
	return new OrderItemResponse(
		orderProduct.getId(),
		orderProduct.getProduct().getName(),
		orderProduct.getCount(),
		orderProduct.getProduct().getPrice(),
		totalPrice,
		discount != null ? discount.calcDiscountAmount(totalPrice) : 0L,
		discount != null ? discount.calcDiscountFinalPrice(totalPrice) : totalPrice
	);
}
	@QueryProjection
	public OrderItemResponse {
	}
}
