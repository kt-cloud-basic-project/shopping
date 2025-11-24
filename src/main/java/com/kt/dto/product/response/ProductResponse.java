package com.kt.dto.product.response;

import com.kt.domain.product.Product;

public record ProductResponse(
	String name,
	String description,
	Long price,
	Long stock,
	String status,
	String category
) {
	public static ProductResponse from(Product product) {
		return new ProductResponse(
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getStock(),
			product.getStatus().getDescription(),
			product.getCategory().getType()
		);
	}
}
