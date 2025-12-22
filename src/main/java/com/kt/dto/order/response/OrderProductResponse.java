package com.kt.dto.order.response;

import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.Product;

public record OrderProductResponse (
	Long productId,
	String productName,
	Long productVariantId,
	Long productCount,
	Long price, // 원 가격
	Long totalPrice, // 총 가격
	Long discountPrice, // 할인 금액
	Long discountedPrice // 할인 후 최종금액

) {
	public static OrderProductResponse from(OrderProduct orderProduct, Product product, Long discountPrice, Long discountedPrice) {
		Long totalPrice = orderProduct.getProduct().getPrice() * orderProduct.getCount();

		return new OrderProductResponse(
			product.getId(),
			product.getName(),
			orderProduct.getVariantId(),
			orderProduct.getCount(),
			orderProduct.getProduct().getPrice(),
			totalPrice,
			discountPrice,
			discountedPrice
		);
	}
}