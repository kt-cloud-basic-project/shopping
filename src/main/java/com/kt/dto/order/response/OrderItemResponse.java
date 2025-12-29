package com.kt.dto.order.response;

import com.kt.domain.orderproduct.OrderProduct;
import com.kt.dto.discount.response.DiscountResult;
import com.querydsl.core.annotations.QueryProjection;

public record OrderItemResponse(
	Long id,
	String name,
	Long count,
	Long price, // 원 가격
	Long totalPrice, // 총 가격
	Long discountPrice, // 할인 금액
	Long discountedPrice // 할인 후 최종금액
) {

	public static OrderItemResponse from(OrderProduct orderProduct, DiscountResult discountResult) {
		Long totalPrice = orderProduct.getProduct().getPrice() * orderProduct.getCount();

		return new OrderItemResponse(
			orderProduct.getId(),
			orderProduct.getProduct().getName(),
			orderProduct.getCount(),
			orderProduct.getProduct().getPrice(),
			totalPrice,
			discountResult.discountPrice(),
			discountResult.discountedPrice()
		);
}
	@QueryProjection
	public OrderItemResponse {
	}
}
