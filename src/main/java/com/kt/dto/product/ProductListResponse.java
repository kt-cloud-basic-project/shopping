package com.kt.dto.product;

import com.kt.domain.product.Product;

public record ProductListResponse(
	String name,
	String category
) {
	public static ProductListResponse from(Product product) {
		return new ProductListResponse(
			product.getName(),
			product.getCategory().getType()
		);
	}
}
