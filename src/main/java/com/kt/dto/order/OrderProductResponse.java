package com.kt.dto.order;

import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.Product;

public record OrderProductResponse (
	Long productId,
	String productName,
	Long productVariantId,
	Long productCount,
	Long productPrice
) {
	public static OrderProductResponse from(OrderProduct orderProduct, Product product) {
		return new OrderProductResponse(
			product.getId(),
			product.getName(),
			orderProduct.getVariantId(),
			orderProduct.getCount(),
			product.getPrice() * orderProduct.getCount()
		);
	}
}