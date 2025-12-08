package com.kt.dto.product.response;

import com.kt.domain.product.Product;

public record UserProductListResponse(
	Long id,
	String name,
	Long price, // 원가
	Long discountAmount, // 할인 금액
	Long discountedPrice, // 할인된 최종 금액
	String category
) {
	public static UserProductListResponse from(Product product, Long discountAmount, Long discountedPrice) {
		return new UserProductListResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			discountAmount,
			discountedPrice,
			product.getCategory().getType()
		);
	}
}