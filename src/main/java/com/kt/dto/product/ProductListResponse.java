package com.kt.dto.product;

import com.kt.domain.product.Product;

public record ProductListResponse(
	String name,
	String description,
	Long price,
	Long stock,
	String category
) {
	public static ProductListResponse from(Product product) {
		return new ProductListResponse(
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getStock(),
			product.getCategory().getType()
		);
	}
}
