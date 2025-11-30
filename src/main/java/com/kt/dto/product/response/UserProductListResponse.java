package com.kt.dto.product.response;

import com.kt.domain.product.Product;

public record UserProductListResponse(
	Long id,
	String name,
	Long price,
	String category
) {
	public static UserProductListResponse from(Product product) {
		return new UserProductListResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getCategory().getType()
		);
	}
}