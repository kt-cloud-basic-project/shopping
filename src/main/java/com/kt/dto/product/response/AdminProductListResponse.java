package com.kt.dto.product.response;

import com.kt.domain.product.Product;

public record AdminProductListResponse(
	String name,
	String category
) {
	public static AdminProductListResponse from(Product product) {
		return new AdminProductListResponse(
			product.getName(),
			product.getCategory().getType()
		);
	}
}
