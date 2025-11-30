package com.kt.dto.product.response;

import java.util.List;

import com.kt.domain.product.Product;
import com.kt.dto.variant.VariantListResponse;

public record UserProductDetailResponse (
	String name,
	String description,
	Long price,
	String category,
	String status,
	List<VariantListResponse> variants
) {
	public static UserProductDetailResponse from(Product product, List<VariantListResponse> variants) {
		return new UserProductDetailResponse(
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getCategory().getType(),
			product.getStatus().getDescription(),
			variants
		);
	}
}

